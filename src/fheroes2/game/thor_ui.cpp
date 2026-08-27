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
#include <utility>

#if defined( ANDROID ) && defined( TARGET_AYN_THOR )
#include <jni.h>
#endif

namespace
{
    std::atomic<fheroes2::thor::UiContext> currentContext{ fheroes2::thor::UiContext::FALLBACK };
    std::atomic<fheroes2::thor::ActionMask> enabledActions{ 0 };
    std::mutex actionQueueMutex;
    std::deque<fheroes2::thor::Action> actionQueue;
    std::mutex informationMutex;
    fheroes2::thor::InformationSnapshot informationSnapshot;

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

    bool isCastleAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::CASTLE_PREVIOUS:
        case Action::CASTLE_NEXT:
        case Action::CASTLE_WELL:
        case Action::CASTLE_MARKETPLACE:
        case Action::CASTLE_MAGE_GUILD:
        case Action::CASTLE_SHIPYARD:
        case Action::CASTLE_THIEVES_GUILD:
        case Action::CASTLE_TAVERN:
        case Action::CASTLE_CONSTRUCTION:
        case Action::CASTLE_TRANSFER_TO_HERO:
        case Action::CASTLE_TRANSFER_TO_GARRISON:
        case Action::CASTLE_UPGRADE_SELECTED:
        case Action::CASTLE_CLOSE:
            return true;
        case Action::NONE:
        default:
            return false;
        }
    }

    bool isNewGameMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::MENU_STANDARD_GAME:
        case Action::MENU_CAMPAIGN_GAME:
        case Action::MENU_MULTIPLAYER_GAME:
        case Action::MENU_BATTLE_ONLY:
        case Action::MENU_SETTINGS:
        case Action::MENU_BACK:
            return true;
        default:
            return false;
        }
    }

    bool isCampaignMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::MENU_ORIGINAL_CAMPAIGN || action == Action::MENU_EXPANSION_CAMPAIGN || action == Action::MENU_BACK;
    }

    bool isMultiplayerMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::MENU_HOT_SEAT || action == Action::MENU_BACK;
    }

    bool isHotSeatMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::MENU_HOT_SEAT_2_PLAYERS:
        case Action::MENU_HOT_SEAT_3_PLAYERS:
        case Action::MENU_HOT_SEAT_4_PLAYERS:
        case Action::MENU_HOT_SEAT_5_PLAYERS:
        case Action::MENU_HOT_SEAT_6_PLAYERS:
        case Action::MENU_BACK:
            return true;
        default:
            return false;
        }
    }

    bool isLoadGameMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::MENU_LOAD_STANDARD || action == Action::MENU_LOAD_CAMPAIGN || action == Action::MENU_LOAD_HOT_SEAT || action == Action::MENU_BACK;
    }

    bool isScenarioSetupAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::SCENARIO_SELECT_MAP:
        case Action::SCENARIO_DIFFICULTY_EASY:
        case Action::SCENARIO_DIFFICULTY_NORMAL:
        case Action::SCENARIO_DIFFICULTY_HARD:
        case Action::SCENARIO_DIFFICULTY_EXPERT:
        case Action::SCENARIO_DIFFICULTY_IMPOSSIBLE:
        case Action::SCENARIO_START:
        case Action::SCENARIO_PREVIOUS_PLAYER:
        case Action::SCENARIO_NEXT_PLAYER:
        case Action::SCENARIO_PLAYER_CONTROL:
        case Action::SCENARIO_PREVIOUS_FACTION:
        case Action::SCENARIO_NEXT_FACTION:
        case Action::SCENARIO_HANDICAP:
        case Action::MENU_BACK:
            return true;
        default:
            return false;
        }
    }

    bool isMainMenuAction( const fheroes2::thor::Action action )
    {
        return action == fheroes2::thor::Action::MENU_EDITOR;
    }

    bool isBattleOnlySetupAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::BATTLE_ONLY_SELECT_ATTACKER:
        case Action::BATTLE_ONLY_SELECT_DEFENDER:
        case Action::BATTLE_ONLY_PREVIOUS_TERRAIN:
        case Action::BATTLE_ONLY_NEXT_TERRAIN:
        case Action::BATTLE_ONLY_TOGGLE_DEFENDER_CONTROL:
        case Action::BATTLE_ONLY_RESET:
        case Action::BATTLE_ONLY_START:
        case Action::BATTLE_ONLY_EXIT:
            return true;
        default:
            return false;
        }
    }

    bool isHighScoresStandardAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::HIGH_SCORES_VIEW_CAMPAIGN || action == Action::HIGH_SCORES_EXIT;
    }

    bool isHighScoresCampaignAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::HIGH_SCORES_VIEW_STANDARD || action == Action::HIGH_SCORES_EXIT;
    }

    bool isSuccessionWarsCampaignAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::CAMPAIGN_SELECT_ROLAND || action == Action::CAMPAIGN_SELECT_ARCHIBALD || action == Action::MENU_BACK;
    }

    bool isPriceOfLoyaltyCampaignAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::CAMPAIGN_SELECT_PRICE_OF_LOYALTY || action == Action::CAMPAIGN_SELECT_VOYAGE_HOME
               || action == Action::CAMPAIGN_SELECT_WIZARDS_ISLE || action == Action::CAMPAIGN_SELECT_DESCENDANTS || action == Action::MENU_BACK;
    }

    bool isGameSettingsAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::GAME_SETTINGS_LANGUAGE:
        case Action::GAME_SETTINGS_GRAPHICS:
        case Action::GAME_SETTINGS_AUDIO:
        case Action::GAME_SETTINGS_HOT_KEYS:
        case Action::GAME_SETTINGS_CURSOR_TYPE:
        case Action::GAME_SETTINGS_INTERFACE_TYPE:
        case Action::GAME_SETTINGS_TEXT_SUPPORT:
        case Action::GAME_SETTINGS_CLOSE:
            return true;
        default:
            return false;
        }
    }

    bool isEditorMainMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::EDITOR_NEW_MAP || action == Action::EDITOR_LOAD_MAP || action == Action::EDITOR_EXIT_TO_MAIN_MENU;
    }

    bool isEditorNewMapMenuAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::EDITOR_FROM_SCRATCH || action == Action::EDITOR_RANDOM_MAP || action == Action::MENU_BACK;
    }

    bool isEditorMapSizeAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::EDITOR_MAP_SIZE_SMALL || action == Action::EDITOR_MAP_SIZE_MEDIUM || action == Action::EDITOR_MAP_SIZE_LARGE
               || action == Action::EDITOR_MAP_SIZE_EXTRA_LARGE || action == Action::MENU_BACK;
    }

    bool isEditorInterfaceAction( const fheroes2::thor::Action action )
    {
        return action == fheroes2::thor::Action::EDITOR_OPEN_FILE_OPTIONS;
    }

    bool isEditorFileOptionsAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::EDITOR_FILE_NEW_MAP:
        case Action::EDITOR_FILE_LOAD_MAP:
        case Action::EDITOR_FILE_START_MAP:
        case Action::EDITOR_FILE_SAVE_MAP:
        case Action::EDITOR_FILE_MAIN_MENU:
        case Action::EDITOR_FILE_QUIT:
        case Action::EDITOR_FILE_AUTO_PLAYTEST:
        case Action::EDITOR_FILE_CANCEL:
            return true;
        default:
            return false;
        }
    }

    bool isSemanticContext( const fheroes2::thor::UiContext context )
    {
        using UiContext = fheroes2::thor::UiContext;

        return context == UiContext::MAIN_MENU || context == UiContext::BATTLE || context == UiContext::ADVENTURE_MAP || context == UiContext::HERO
               || context == UiContext::CASTLE
               || context == UiContext::NEW_GAME_MENU || context == UiContext::CAMPAIGN_MENU || context == UiContext::MULTIPLAYER_MENU
               || context == UiContext::HOT_SEAT_MENU || context == UiContext::LOAD_GAME_MENU || context == UiContext::SCENARIO_SETUP
               || context == UiContext::BATTLE_ONLY_SETUP || context == UiContext::HIGH_SCORES_STANDARD
               || context == UiContext::HIGH_SCORES_CAMPAIGN || context == UiContext::SUCCESSION_WARS_CAMPAIGN
               || context == UiContext::PRICE_OF_LOYALTY_CAMPAIGN || context == UiContext::GAME_SETTINGS || context == UiContext::EDITOR_MAIN_MENU
               || context == UiContext::EDITOR_NEW_MAP_MENU || context == UiContext::EDITOR_MAP_SIZE_SCRATCH
               || context == UiContext::EDITOR_MAP_SIZE_RANDOM || context == UiContext::EDITOR_INTERFACE
               || context == UiContext::EDITOR_FILE_OPTIONS;
    }

    bool isActionValidForContext( const fheroes2::thor::Action action, const fheroes2::thor::UiContext context )
    {
        switch ( context ) {
        case fheroes2::thor::UiContext::MAIN_MENU:
            return isMainMenuAction( action );
        case fheroes2::thor::UiContext::BATTLE:
            return isBattleAction( action );
        case fheroes2::thor::UiContext::ADVENTURE_MAP:
            return isAdventureAction( action );
        case fheroes2::thor::UiContext::HERO:
            return isHeroAction( action );
        case fheroes2::thor::UiContext::CASTLE:
            return isCastleAction( action );
        case fheroes2::thor::UiContext::NEW_GAME_MENU:
            return isNewGameMenuAction( action );
        case fheroes2::thor::UiContext::CAMPAIGN_MENU:
            return isCampaignMenuAction( action );
        case fheroes2::thor::UiContext::MULTIPLAYER_MENU:
            return isMultiplayerMenuAction( action );
        case fheroes2::thor::UiContext::HOT_SEAT_MENU:
            return isHotSeatMenuAction( action );
        case fheroes2::thor::UiContext::LOAD_GAME_MENU:
            return isLoadGameMenuAction( action );
        case fheroes2::thor::UiContext::SCENARIO_SETUP:
            return isScenarioSetupAction( action );
        case fheroes2::thor::UiContext::BATTLE_ONLY_SETUP:
            return isBattleOnlySetupAction( action );
        case fheroes2::thor::UiContext::HIGH_SCORES_STANDARD:
            return isHighScoresStandardAction( action );
        case fheroes2::thor::UiContext::HIGH_SCORES_CAMPAIGN:
            return isHighScoresCampaignAction( action );
        case fheroes2::thor::UiContext::SUCCESSION_WARS_CAMPAIGN:
            return isSuccessionWarsCampaignAction( action );
        case fheroes2::thor::UiContext::PRICE_OF_LOYALTY_CAMPAIGN:
            return isPriceOfLoyaltyCampaignAction( action );
        case fheroes2::thor::UiContext::GAME_SETTINGS:
            return isGameSettingsAction( action );
        case fheroes2::thor::UiContext::EDITOR_MAIN_MENU:
            return isEditorMainMenuAction( action );
        case fheroes2::thor::UiContext::EDITOR_NEW_MAP_MENU:
            return isEditorNewMapMenuAction( action );
        case fheroes2::thor::UiContext::EDITOR_MAP_SIZE_SCRATCH:
        case fheroes2::thor::UiContext::EDITOR_MAP_SIZE_RANDOM:
            return isEditorMapSizeAction( action );
        case fheroes2::thor::UiContext::EDITOR_INTERFACE:
            return isEditorInterfaceAction( action );
        case fheroes2::thor::UiContext::EDITOR_FILE_OPTIONS:
            return isEditorFileOptionsAction( action );
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

            std::lock_guard<std::mutex> informationLock( informationMutex );
            InformationSnapshot emptySnapshot;
            emptySnapshot.context = context;
            emptySnapshot.revision = informationSnapshot.revision + 1;
            informationSnapshot = std::move( emptySnapshot );
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
        const ActionMask allowedActions = isSemanticContext( context ) ? actions : 0;
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

    InformationSnapshot getInformationSnapshot()
    {
        std::lock_guard<std::mutex> lock( informationMutex );
        return informationSnapshot;
    }

    void publishInformationSnapshot( InformationSnapshot snapshot )
    {
        std::lock_guard<std::mutex> lock( informationMutex );
        if ( informationSnapshot.version == snapshot.version && informationSnapshot.context == snapshot.context && informationSnapshot.title == snapshot.title
             && informationSnapshot.category == snapshot.category && informationSnapshot.detail == snapshot.detail && informationSnapshot.date == snapshot.date
             && informationSnapshot.resources == snapshot.resources ) {
            return;
        }

        snapshot.revision = informationSnapshot.revision + 1;
        informationSnapshot = std::move( snapshot );
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

extern "C" JNIEXPORT jobjectArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorInformationSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    const fheroes2::thor::InformationSnapshot snapshot = fheroes2::thor::getInformationSnapshot();
    if ( snapshot.revision == static_cast<uint64_t>( knownRevision ) ) {
        return nullptr;
    }

    jclass stringClass = env->FindClass( "java/lang/String" );
    if ( stringClass == nullptr ) {
        return nullptr;
    }

    constexpr jsize fieldCount = 8;
    jobjectArray fields = env->NewObjectArray( fieldCount, stringClass, nullptr );
    if ( fields == nullptr ) {
        env->DeleteLocalRef( stringClass );
        return nullptr;
    }

    const std::string version = std::to_string( snapshot.version );
    const std::string context = std::to_string( static_cast<int32_t>( snapshot.context ) );
    const std::string revision = std::to_string( snapshot.revision );
    const std::string * values[fieldCount]
        = { &version, &context, &revision, &snapshot.title, &snapshot.category, &snapshot.detail, &snapshot.date, &snapshot.resources };

    for ( jsize index = 0; index < fieldCount; ++index ) {
        jstring value = env->NewStringUTF( values[index]->c_str() );
        if ( value == nullptr ) {
            env->DeleteLocalRef( stringClass );
            return nullptr;
        }
        env->SetObjectArrayElement( fields, index, value );
        env->DeleteLocalRef( value );
    }

    env->DeleteLocalRef( stringClass );
    return fields;
}
#endif
