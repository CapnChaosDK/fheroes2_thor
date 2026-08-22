/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

#include "thor_ui.h"

#include <atomic>
#include <deque>
#include <mutex>

#if defined( ANDROID ) && defined( TARGET_AYN_THOR )
#include <jni.h>
#endif

namespace
{
    std::atomic<fheroes2::thor::UiContext> currentContext{ fheroes2::thor::UiContext::FALLBACK };
    std::atomic<fheroes2::thor::ActionMask> enabledActions{ 0 };
    std::mutex actionQueueMutex;
    std::deque<fheroes2::thor::Action> actionQueue;

    bool isBattleAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::BATTLE_CAST_SPELL:
        case Action::BATTLE_SKIP:
        case Action::BATTLE_TOGGLE_AUTO_COMBAT:
        case Action::BATTLE_QUICK_COMBAT:
        case Action::BATTLE_RETREAT:
        case Action::BATTLE_SURRENDER:
        case Action::BATTLE_OPTIONS:
        case Action::BATTLE_TOGGLE_TURN_ORDER:
            return true;
        case Action::NONE:
        default:
            return false;
        }
    }

    bool isAdventureAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::ADVENTURE_NEXT_HERO:
        case Action::ADVENTURE_NEXT_TOWN:
        case Action::ADVENTURE_MOVE:
        case Action::ADVENTURE_DEFAULT_ACTION:
        case Action::ADVENTURE_CAST_SPELL:
        case Action::ADVENTURE_END_TURN:
        case Action::ADVENTURE_OPTIONS:
        case Action::ADVENTURE_FILE_OPTIONS:
        case Action::ADVENTURE_PUZZLE_MAP:
        case Action::ADVENTURE_KINGDOM_SUMMARY:
        case Action::ADVENTURE_VIEW_WORLD:
        case Action::ADVENTURE_DIG_ARTIFACT:
            return true;
        case Action::NONE:
        default:
            return false;
        }
    }

    bool isHeroAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::HERO_PREVIOUS:
        case Action::HERO_NEXT:
        case Action::HERO_DISMISS:
        case Action::HERO_UPGRADE_SELECTED:
        case Action::HERO_SPLIT_SELECTED_HALF:
        case Action::HERO_SPLIT_SELECTED_ONE:
        case Action::HERO_JOIN_SELECTED:
        case Action::HERO_SWAP_ARMIES:
        case Action::HERO_CLOSE:
            return true;
        case Action::NONE:
        default:
            return false;
        }
    }

    bool isActionValidForContext( const fheroes2::thor::Action action, const fheroes2::thor::UiContext context )
    {
        switch ( context ) {
        case fheroes2::thor::UiContext::BATTLE:
            return isBattleAction( action );
        case fheroes2::thor::UiContext::ADVENTURE_MAP:
            return isAdventureAction( action );
        case fheroes2::thor::UiContext::HERO:
            return isHeroAction( action );
        default:
            return false;
        }
    }
}

namespace fheroes2::thor
{
    UiContext getUiContext()
    {
        return currentContext.load( std::memory_order_acquire );
    }

    void setUiContext( const UiContext context )
    {
        std::lock_guard<std::mutex> lock( actionQueueMutex );
        const UiContext previousContext = currentContext.exchange( context, std::memory_order_acq_rel );
        if ( previousContext != context ) {
            enabledActions.store( 0, std::memory_order_release );
            actionQueue.clear();
        }
    }

    bool enqueueAction( const Action action )
    {
        const UiContext context = getUiContext();
        if ( !isActionValidForContext( action, context ) || ( getEnabledActions() & actionMask( action ) ) == 0 ) {
            return false;
        }

        std::lock_guard<std::mutex> lock( actionQueueMutex );

        // The context may have changed while this thread was waiting for the queue lock.
        const UiContext lockedContext = getUiContext();
        if ( !isActionValidForContext( action, lockedContext ) || ( getEnabledActions() & actionMask( action ) ) == 0 ) {
            return false;
        }

        // Avoid retaining an unbounded number of stale taps if the game thread is paused.
        constexpr size_t maximumQueuedActions = 16;
        if ( actionQueue.size() >= maximumQueuedActions ) {
            return false;
        }

        actionQueue.emplace_back( action );
        return true;
    }

    Action takeAction()
    {
        std::lock_guard<std::mutex> lock( actionQueueMutex );
        if ( actionQueue.empty() ) {
            return Action::NONE;
        }

        while ( !actionQueue.empty() ) {
            const Action action = actionQueue.front();
            actionQueue.pop_front();
            if ( ( getEnabledActions() & actionMask( action ) ) != 0 ) {
                return action;
            }
        }

        return Action::NONE;
    }

    ActionMask getEnabledActions()
    {
        return enabledActions.load( std::memory_order_acquire );
    }

    void setEnabledActions( const ActionMask actions )
    {
        std::lock_guard<std::mutex> lock( actionQueueMutex );
        const UiContext context = getUiContext();
        const ActionMask allowedActions = context == UiContext::BATTLE || context == UiContext::ADVENTURE_MAP || context == UiContext::HERO ? actions : 0;
        enabledActions.store( allowedActions, std::memory_order_release );
        for ( auto actionIter = actionQueue.begin(); actionIter != actionQueue.end(); ) {
            if ( ( allowedActions & actionMask( *actionIter ) ) == 0 ) {
                actionIter = actionQueue.erase( actionIter );
            }
            else {
                ++actionIter;
            }
        }
    }

    UiContextGuard::UiContextGuard( const UiContext context )
        : _previousContext( getUiContext() )
    {
        setUiContext( context );
    }

    UiContextGuard::~UiContextGuard()
    {
        setUiContext( _previousContext );
    }
}

#if defined( ANDROID ) && defined( TARGET_AYN_THOR )
extern "C" JNIEXPORT jint JNICALL Java_org_fheroes2_GameActivity_nativeGetThorUiContext( JNIEnv *, jclass )
{
    return static_cast<jint>( fheroes2::thor::getUiContext() );
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeEnqueueThorAction( JNIEnv *, jclass, const jint action )
{
    return fheroes2::thor::enqueueAction( static_cast<fheroes2::thor::Action>( action ) ) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jlong JNICALL Java_org_fheroes2_GameActivity_nativeGetThorEnabledActionMask( JNIEnv *, jclass )
{
    return static_cast<jlong>( fheroes2::thor::getEnabledActions() );
}
#endif
