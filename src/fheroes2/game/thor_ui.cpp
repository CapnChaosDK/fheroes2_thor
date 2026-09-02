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

#include <algorithm>
#include <atomic>
#include <cmath>
#include <deque>
#include <limits>
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
    std::mutex radarMutex;
    fheroes2::thor::RadarSnapshot radarSnapshot;
    std::mutex visualMutex;
    fheroes2::thor::VisualSnapshot visualSnapshot;
    std::mutex viewportRequestMutex;
    fheroes2::thor::ViewportRequest viewportRequest;
    std::atomic<bool> viewportControlEnabled{ false };
    std::mutex selectionMutex;
    fheroes2::thor::SelectionSnapshot selectionSnapshot;
    fheroes2::thor::SelectionRequest selectionRequest;
    fheroes2::thor::MarkerInfoRequest markerInfoRequest;
    std::mutex troopMutex;
    fheroes2::thor::TroopSnapshot troopSnapshot;
    fheroes2::thor::TroopTransferRequest troopTransferRequest;

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
        case Action::ADVENTURE_OPEN_HERO_LIST:
        case Action::ADVENTURE_OPEN_CASTLE_LIST:
        case Action::ADVENTURE_OPEN_MAP_OVERVIEW:
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

    bool isHeroMeetingAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::HERO_MEETING_TRANSFER_TO_RIGHT:
        case Action::HERO_MEETING_TRANSFER_TO_LEFT:
        case Action::HERO_MEETING_SWAP_ARMIES:
        case Action::HERO_MEETING_CLOSE:
            return true;
        default:
            return false;
        }
    }

    bool isAdventureSelectionAction( const fheroes2::thor::Action action )
    {
        return action == fheroes2::thor::Action::ADVENTURE_SELECTION_BACK;
    }

    bool isAdventureOverviewAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::ADVENTURE_OVERVIEW_OPEN_HERO_LIST || action == Action::ADVENTURE_OVERVIEW_OPEN_CASTLE_LIST
               || action == Action::ADVENTURE_OVERVIEW_BACK;
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
        return action == fheroes2::thor::Action::EDITOR_OPEN_FILE_OPTIONS || action == fheroes2::thor::Action::EDITOR_OPEN_SYSTEM_OPTIONS
               || action == fheroes2::thor::Action::EDITOR_OPEN_MAP_SPECIFICATIONS || action == fheroes2::thor::Action::EDITOR_OPEN_TOOLS;
    }

    bool isEditorToolsAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::EDITOR_TOOL_TERRAIN:
        case Action::EDITOR_TOOL_LANDSCAPE:
        case Action::EDITOR_TOOL_DETAIL:
        case Action::EDITOR_TOOL_ADVENTURE:
        case Action::EDITOR_TOOL_KINGDOM:
        case Action::EDITOR_TOOL_MONSTERS:
        case Action::EDITOR_TOOL_STREAMS:
        case Action::EDITOR_TOOL_ROADS:
        case Action::EDITOR_TOOL_ERASE:
        case Action::EDITOR_TOOL_MAGNIFY:
        case Action::EDITOR_TOOL_UNDO:
        case Action::EDITOR_TOOL_REDO:
        case Action::EDITOR_TOOL_BACK:
            return true;
        default:
            return false;
        }
    }

    bool isEditorBrushAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::EDITOR_BRUSH_SMALL || action == Action::EDITOR_BRUSH_MEDIUM || action == Action::EDITOR_BRUSH_LARGE
               || action == Action::EDITOR_BRUSH_AREA;
    }

    bool isEditorToolSubpanelAction( const fheroes2::thor::Action action, const fheroes2::thor::UiContext context )
    {
        using Action = fheroes2::thor::Action;
        using UiContext = fheroes2::thor::UiContext;

        if ( action == Action::EDITOR_TOOL_BACK ) {
            return true;
        }

        switch ( context ) {
        case UiContext::EDITOR_TOOL_TERRAIN:
            return isEditorBrushAction( action )
                   || ( action >= Action::EDITOR_TERRAIN_WATER && action <= Action::EDITOR_TERRAIN_BEACH );
        case UiContext::EDITOR_TOOL_LANDSCAPE:
            return action >= Action::EDITOR_LANDSCAPE_MOUNTAINS && action <= Action::EDITOR_LANDSCAPE_MISC;
        case UiContext::EDITOR_TOOL_DETAIL:
            return action >= Action::EDITOR_DETAIL_EDIT && action <= Action::EDITOR_DETAIL_COPY;
        case UiContext::EDITOR_TOOL_ADVENTURE:
            return action >= Action::EDITOR_ADVENTURE_ARTIFACTS && action <= Action::EDITOR_ADVENTURE_MISC;
        case UiContext::EDITOR_TOOL_KINGDOM:
            return action == Action::EDITOR_KINGDOM_HEROES || action == Action::EDITOR_KINGDOM_TOWNS;
        case UiContext::EDITOR_TOOL_MONSTERS:
            return action == Action::EDITOR_MONSTER_SELECT;
        case UiContext::EDITOR_TOOL_STREAMS:
        case UiContext::EDITOR_TOOL_ROADS:
            return false;
        case UiContext::EDITOR_TOOL_ERASE:
            return isEditorBrushAction( action ) || ( action >= Action::EDITOR_ERASE_MOUNTAINS && action <= Action::EDITOR_ERASE_STREAMS );
        default:
            return false;
        }
    }

    bool isEditorSystemOptionsAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::EDITOR_SYSTEM_LANGUAGE:
        case Action::EDITOR_SYSTEM_GRAPHICS:
        case Action::EDITOR_SYSTEM_AUDIO:
        case Action::EDITOR_SYSTEM_HOT_KEYS:
        case Action::EDITOR_SYSTEM_ANIMATION:
        case Action::EDITOR_SYSTEM_PASSABILITY:
        case Action::EDITOR_SYSTEM_INTERFACE_TYPE:
        case Action::EDITOR_SYSTEM_CURSOR_TYPE:
        case Action::EDITOR_SYSTEM_SCROLL_SPEED:
        case Action::EDITOR_SYSTEM_CLOSE:
            return true;
        default:
            return false;
        }
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

    bool isEditorMapSpecificationsAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::EDITOR_MAP_SPEC_NAME:
        case Action::EDITOR_MAP_SPEC_DESCRIPTION:
        case Action::EDITOR_MAP_SPEC_PLAYERS:
        case Action::EDITOR_MAP_SPEC_DIFFICULTY:
        case Action::EDITOR_MAP_SPEC_VICTORY:
        case Action::EDITOR_MAP_SPEC_LOSS:
        case Action::EDITOR_MAP_SPEC_RUMORS:
        case Action::EDITOR_MAP_SPEC_EVENTS:
        case Action::EDITOR_MAP_SPEC_LANGUAGE:
        case Action::EDITOR_MAP_SPEC_ABOUT:
        case Action::EDITOR_MAP_SPEC_OKAY:
        case Action::EDITOR_MAP_SPEC_CANCEL:
            return true;
        default:
            return false;
        }
    }

    bool isEditorMapSpecPlayersAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        return action == Action::EDITOR_MAP_SPEC_PREVIOUS_PLAYER || action == Action::EDITOR_MAP_SPEC_NEXT_PLAYER
               || action == Action::EDITOR_MAP_SPEC_PLAYER_TYPE || action == Action::EDITOR_MAP_SPEC_SUBMENU_BACK;
    }

    bool isEditorMapSpecConditionAction( const fheroes2::thor::Action action )
    {
        using Action = fheroes2::thor::Action;

        switch ( action ) {
        case Action::EDITOR_MAP_SPEC_PREVIOUS_CONDITION:
        case Action::EDITOR_MAP_SPEC_NEXT_CONDITION:
        case Action::EDITOR_MAP_SPEC_SELECT_TARGET:
        case Action::EDITOR_MAP_SPEC_TOGGLE_STANDARD_VICTORY:
        case Action::EDITOR_MAP_SPEC_TOGGLE_AI_VICTORY:
        case Action::EDITOR_MAP_SPEC_PREVIOUS_ALLIANCE_PLAYER:
        case Action::EDITOR_MAP_SPEC_NEXT_ALLIANCE_PLAYER:
        case Action::EDITOR_MAP_SPEC_SWITCH_ALLIANCE:
        case Action::EDITOR_MAP_SPEC_DECREASE_VALUE:
        case Action::EDITOR_MAP_SPEC_INCREASE_VALUE:
        case Action::EDITOR_MAP_SPEC_SUBMENU_BACK:
            return true;
        default:
            return false;
        }
    }

    bool isSemanticContext( const fheroes2::thor::UiContext context )
    {
        using UiContext = fheroes2::thor::UiContext;

        return context == UiContext::MAIN_MENU || context == UiContext::BATTLE || context == UiContext::ADVENTURE_MAP || context == UiContext::HERO
               || context == UiContext::CASTLE || context == UiContext::HERO_MEETING || context == UiContext::ADVENTURE_HERO_LIST
               || context == UiContext::ADVENTURE_CASTLE_LIST
               || context == UiContext::ADVENTURE_MAP_OVERVIEW
               || context == UiContext::NEW_GAME_MENU || context == UiContext::CAMPAIGN_MENU || context == UiContext::MULTIPLAYER_MENU
               || context == UiContext::HOT_SEAT_MENU || context == UiContext::LOAD_GAME_MENU || context == UiContext::SCENARIO_SETUP
               || context == UiContext::BATTLE_ONLY_SETUP || context == UiContext::HIGH_SCORES_STANDARD
               || context == UiContext::HIGH_SCORES_CAMPAIGN || context == UiContext::SUCCESSION_WARS_CAMPAIGN
               || context == UiContext::PRICE_OF_LOYALTY_CAMPAIGN || context == UiContext::GAME_SETTINGS || context == UiContext::EDITOR_MAIN_MENU
               || context == UiContext::EDITOR_NEW_MAP_MENU || context == UiContext::EDITOR_MAP_SIZE_SCRATCH
               || context == UiContext::EDITOR_MAP_SIZE_RANDOM || context == UiContext::EDITOR_INTERFACE
               || context == UiContext::EDITOR_FILE_OPTIONS || context == UiContext::EDITOR_SYSTEM_OPTIONS
               || context == UiContext::EDITOR_MAP_SPECIFICATIONS || context == UiContext::EDITOR_MAP_SPEC_PLAYERS
               || context == UiContext::EDITOR_MAP_SPEC_VICTORY || context == UiContext::EDITOR_MAP_SPEC_LOSS || context == UiContext::EDITOR_TOOLS
               || ( context >= UiContext::EDITOR_TOOL_TERRAIN && context <= UiContext::EDITOR_TOOL_ERASE );
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
        case fheroes2::thor::UiContext::ADVENTURE_HERO_LIST:
        case fheroes2::thor::UiContext::ADVENTURE_CASTLE_LIST:
            return isAdventureSelectionAction( action );
        case fheroes2::thor::UiContext::ADVENTURE_MAP_OVERVIEW:
            return isAdventureOverviewAction( action );
        case fheroes2::thor::UiContext::HERO:
            return isHeroAction( action );
        case fheroes2::thor::UiContext::CASTLE:
            return isCastleAction( action );
        case fheroes2::thor::UiContext::HERO_MEETING:
            return isHeroMeetingAction( action );
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
        case fheroes2::thor::UiContext::EDITOR_SYSTEM_OPTIONS:
            return isEditorSystemOptionsAction( action );
        case fheroes2::thor::UiContext::EDITOR_MAP_SPECIFICATIONS:
            return isEditorMapSpecificationsAction( action );
        case fheroes2::thor::UiContext::EDITOR_MAP_SPEC_PLAYERS:
            return isEditorMapSpecPlayersAction( action );
        case fheroes2::thor::UiContext::EDITOR_MAP_SPEC_VICTORY:
        case fheroes2::thor::UiContext::EDITOR_MAP_SPEC_LOSS:
            return isEditorMapSpecConditionAction( action );
        case fheroes2::thor::UiContext::EDITOR_TOOLS:
            return isEditorToolsAction( action );
        case fheroes2::thor::UiContext::EDITOR_TOOL_TERRAIN:
        case fheroes2::thor::UiContext::EDITOR_TOOL_LANDSCAPE:
        case fheroes2::thor::UiContext::EDITOR_TOOL_DETAIL:
        case fheroes2::thor::UiContext::EDITOR_TOOL_ADVENTURE:
        case fheroes2::thor::UiContext::EDITOR_TOOL_KINGDOM:
        case fheroes2::thor::UiContext::EDITOR_TOOL_MONSTERS:
        case fheroes2::thor::UiContext::EDITOR_TOOL_STREAMS:
        case fheroes2::thor::UiContext::EDITOR_TOOL_ROADS:
        case fheroes2::thor::UiContext::EDITOR_TOOL_ERASE:
            return isEditorToolSubpanelAction( action, context );
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
            viewportControlEnabled.store( false, std::memory_order_release );

            {
                std::lock_guard<std::mutex> viewportLock( viewportRequestMutex );
                viewportRequest = {};
            }

            {
                std::lock_guard<std::mutex> selectionLock( selectionMutex );
                selectionRequest = {};
                markerInfoRequest = {};
                SelectionSnapshot emptySelection;
                emptySelection.context = context;
                emptySelection.revision = selectionSnapshot.revision + 1;
                selectionSnapshot = std::move( emptySelection );
            }

            {
                std::lock_guard<std::mutex> visualLock( visualMutex );
                VisualSnapshot emptyVisual;
                emptyVisual.context = context;
                emptyVisual.revision = visualSnapshot.revision + 1;
                visualSnapshot = std::move( emptyVisual );
            }

            {
                std::lock_guard<std::mutex> troopLock( troopMutex );
                troopTransferRequest = {};
                TroopSnapshot emptyTroops;
                emptyTroops.context = context;
                emptyTroops.revision = troopSnapshot.revision + 1;
                troopSnapshot = std::move( emptyTroops );
            }

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

    bool getRadarSnapshot( const uint64_t knownRevision, RadarSnapshot & snapshot )
    {
        std::lock_guard<std::mutex> lock( radarMutex );
        if ( radarSnapshot.revision == knownRevision ) {
            return false;
        }

        snapshot = radarSnapshot;
        return true;
    }

    void publishRadarSnapshot( RadarSnapshot snapshot )
    {
        std::lock_guard<std::mutex> lock( radarMutex );
        if ( radarSnapshot.version == snapshot.version && radarSnapshot.context == snapshot.context && radarSnapshot.width == snapshot.width
             && radarSnapshot.height == snapshot.height && radarSnapshot.worldWidth == snapshot.worldWidth && radarSnapshot.worldHeight == snapshot.worldHeight
             && radarSnapshot.viewportX == snapshot.viewportX && radarSnapshot.viewportY == snapshot.viewportY
             && radarSnapshot.viewportWidth == snapshot.viewportWidth && radarSnapshot.viewportHeight == snapshot.viewportHeight
             && radarSnapshot.pixels == snapshot.pixels ) {
            return;
        }

        snapshot.revision = radarSnapshot.revision + 1;
        radarSnapshot = std::move( snapshot );
    }

    bool getVisualSnapshot( const uint64_t knownRevision, VisualSnapshot & snapshot )
    {
        std::lock_guard<std::mutex> lock( visualMutex );
        if ( visualSnapshot.revision == knownRevision ) {
            return false;
        }

        snapshot = visualSnapshot;
        return true;
    }

    void publishVisualSnapshot( VisualSnapshot snapshot )
    {
        constexpr int32_t maximumVisualDimension = 256;
        if ( snapshot.width < 0 || snapshot.height < 0 || snapshot.width > maximumVisualDimension || snapshot.height > maximumVisualDimension
             || snapshot.pixels.size() != static_cast<size_t>( snapshot.width ) * snapshot.height ) {
            snapshot.width = 0;
            snapshot.height = 0;
            snapshot.pixels.clear();
        }

        std::lock_guard<std::mutex> lock( visualMutex );
        if ( visualSnapshot.version == snapshot.version && visualSnapshot.context == snapshot.context && visualSnapshot.width == snapshot.width
             && visualSnapshot.height == snapshot.height && visualSnapshot.pixels == snapshot.pixels ) {
            return;
        }

        snapshot.revision = visualSnapshot.revision + 1;
        visualSnapshot = std::move( snapshot );
    }

    bool enqueueViewportRequest( const float normalizedX, const float normalizedY )
    {
        if ( !std::isfinite( normalizedX ) || !std::isfinite( normalizedY ) || normalizedX < 0.0F || normalizedX > 1.0F || normalizedY < 0.0F
             || normalizedY > 1.0F || !isViewportControlEnabled() ) {
            return false;
        }

        const UiContext context = getUiContext();
        if ( context != UiContext::ADVENTURE_MAP && context != UiContext::ADVENTURE_MAP_OVERVIEW && context != UiContext::EDITOR_INTERFACE ) {
            return false;
        }

        std::lock_guard<std::mutex> lock( viewportRequestMutex );
        if ( context != getUiContext() || !isViewportControlEnabled() ) {
            return false;
        }

        viewportRequest = { context, normalizedX, normalizedY, true };
        return true;
    }

    ViewportRequest takeViewportRequest()
    {
        std::lock_guard<std::mutex> lock( viewportRequestMutex );
        ViewportRequest request = viewportRequest;
        viewportRequest = {};
        if ( !request.valid || request.context != getUiContext() || !isViewportControlEnabled() ) {
            return {};
        }

        return request;
    }

    bool isViewportControlEnabled()
    {
        return viewportControlEnabled.load( std::memory_order_acquire );
    }

    void setViewportControlEnabled( const bool enabled )
    {
        const UiContext context = getUiContext();
        const bool allowed
            = enabled && ( context == UiContext::ADVENTURE_MAP || context == UiContext::ADVENTURE_MAP_OVERVIEW || context == UiContext::EDITOR_INTERFACE );
        viewportControlEnabled.store( allowed, std::memory_order_release );
        if ( !allowed ) {
            std::lock_guard<std::mutex> lock( viewportRequestMutex );
            viewportRequest = {};
        }
    }

    bool getSelectionSnapshot( const uint64_t knownRevision, SelectionSnapshot & snapshot )
    {
        std::lock_guard<std::mutex> lock( selectionMutex );
        if ( selectionSnapshot.revision == knownRevision ) {
            return false;
        }

        snapshot = selectionSnapshot;
        return true;
    }

    void publishSelectionSnapshot( SelectionSnapshot snapshot )
    {
        std::lock_guard<std::mutex> lock( selectionMutex );
        const bool unchanged = selectionSnapshot.version == snapshot.version && selectionSnapshot.context == snapshot.context
                               && selectionSnapshot.entries.size() == snapshot.entries.size()
                               && std::equal( selectionSnapshot.entries.begin(), selectionSnapshot.entries.end(), snapshot.entries.begin(),
                                              []( const SelectionEntry & left, const SelectionEntry & right ) {
                                                  return left.id == right.id && left.name == right.name && left.detail == right.detail
                                                         && left.selected == right.selected && left.kind == right.kind && left.x == right.x && left.y == right.y
                                                         && left.relationship == right.relationship && left.selectable == right.selectable;
                                              } );
        if ( unchanged ) {
            return;
        }

        snapshot.revision = selectionSnapshot.revision + 1;
        selectionSnapshot = std::move( snapshot );
        selectionRequest = {};
        markerInfoRequest = {};
    }

    bool enqueueSelectionRequest( const UiContext context, const uint64_t revision, const SelectionEntry::Kind kind, const int32_t id )
    {
        if ( context != UiContext::ADVENTURE_HERO_LIST && context != UiContext::ADVENTURE_CASTLE_LIST && context != UiContext::ADVENTURE_MAP_OVERVIEW ) {
            return false;
        }

        std::lock_guard<std::mutex> lock( selectionMutex );
        if ( getUiContext() != context || selectionSnapshot.context != context || selectionSnapshot.revision != revision ) {
            return false;
        }

        const auto entry = std::find_if( selectionSnapshot.entries.begin(), selectionSnapshot.entries.end(), [kind, id]( const SelectionEntry & value ) {
            return value.kind == kind && value.id == id;
        } );
        if ( entry == selectionSnapshot.entries.end() || !entry->selectable ) {
            return false;
        }

        selectionRequest = { context, revision, id, kind, true };
        return true;
    }

    SelectionRequest takeSelectionRequest()
    {
        std::lock_guard<std::mutex> lock( selectionMutex );
        SelectionRequest request = selectionRequest;
        selectionRequest = {};
        if ( !request.valid || request.context != getUiContext() || request.context != selectionSnapshot.context
             || request.revision != selectionSnapshot.revision ) {
            return {};
        }
        return request;
    }

    bool enqueueMarkerInfoRequest( const UiContext context, const uint64_t revision, const SelectionEntry::Kind kind, const int32_t id )
    {
        if ( context != UiContext::ADVENTURE_MAP_OVERVIEW ) {
            return false;
        }

        std::lock_guard<std::mutex> lock( selectionMutex );
        if ( getUiContext() != context || selectionSnapshot.context != context || selectionSnapshot.revision != revision ) {
            return false;
        }

        if ( id < 0 ) {
            markerInfoRequest = { context, revision, id, kind, SelectionEntry::Relationship::OWNED, -1, true, true };
            return true;
        }

        const auto entry = std::find_if( selectionSnapshot.entries.begin(), selectionSnapshot.entries.end(), [kind, id]( const SelectionEntry & value ) {
            return value.kind == kind && value.id == id;
        } );
        if ( entry == selectionSnapshot.entries.end() ) {
            return false;
        }

        markerInfoRequest = { context, revision, id, kind, entry->relationship, -1, false, true };
        return true;
    }

    MarkerInfoRequest takeMarkerInfoRequest()
    {
        std::lock_guard<std::mutex> lock( selectionMutex );
        MarkerInfoRequest request = markerInfoRequest;
        markerInfoRequest = {};
        if ( !request.valid || request.context != getUiContext() || request.context != selectionSnapshot.context
             || request.revision != selectionSnapshot.revision ) {
            return {};
        }
        return request;
    }

    bool getTroopSnapshot( const uint64_t knownRevision, TroopSnapshot & snapshot )
    {
        std::lock_guard<std::mutex> lock( troopMutex );
        if ( troopSnapshot.revision == knownRevision ) {
            return false;
        }

        snapshot = troopSnapshot;
        return true;
    }

    void publishTroopSnapshot( TroopSnapshot snapshot )
    {
        constexpr size_t expectedSlotCount = 10;
        constexpr int32_t maximumSpriteDimension = 64;
        if ( snapshot.context != UiContext::HERO_MEETING || snapshot.slots.size() != expectedSlotCount ) {
            snapshot.slots.clear();
        }
        for ( TroopSlotSnapshot & slot : snapshot.slots ) {
            if ( slot.width < 0 || slot.height < 0 || slot.width > maximumSpriteDimension || slot.height > maximumSpriteDimension
                 || slot.pixels.size() != static_cast<size_t>( slot.width ) * slot.height ) {
                slot.width = 0;
                slot.height = 0;
                slot.pixels.clear();
            }
        }

        std::lock_guard<std::mutex> lock( troopMutex );
        const bool unchanged = troopSnapshot.version == snapshot.version && troopSnapshot.context == snapshot.context && troopSnapshot.leftHero == snapshot.leftHero
                               && troopSnapshot.rightHero == snapshot.rightHero && troopSnapshot.upperSelectedSide == snapshot.upperSelectedSide
                               && troopSnapshot.upperSelectedSlot == snapshot.upperSelectedSlot && troopSnapshot.slots.size() == snapshot.slots.size()
                               && std::equal( troopSnapshot.slots.begin(), troopSnapshot.slots.end(), snapshot.slots.begin(),
                                              []( const TroopSlotSnapshot & left, const TroopSlotSnapshot & right ) {
                                                  return left.monsterId == right.monsterId && left.count == right.count && left.name == right.name
                                                         && left.width == right.width && left.height == right.height && left.pixels == right.pixels;
                                              } );
        if ( unchanged ) {
            return;
        }

        snapshot.revision = troopSnapshot.revision + 1;
        troopSnapshot = std::move( snapshot );
        troopTransferRequest = {};
    }

    bool enqueueTroopTransferRequest( const UiContext context, const uint64_t revision, const int32_t sourceSide, const int32_t sourceSlot, const int32_t destinationSide,
                                      const int32_t destinationSlot )
    {
        constexpr int32_t slotCountPerSide = 5;
        if ( context != UiContext::HERO_MEETING || sourceSide < 0 || sourceSide > 1 || destinationSide < 0 || destinationSide > 1 || sourceSide == destinationSide
             || sourceSlot < 0 || sourceSlot >= slotCountPerSide || destinationSlot < 0 || destinationSlot >= slotCountPerSide ) {
            return false;
        }

        std::lock_guard<std::mutex> lock( troopMutex );
        const size_t sourceIndex = static_cast<size_t>( sourceSide * slotCountPerSide + sourceSlot );
        if ( getUiContext() != context || troopSnapshot.context != context || troopSnapshot.revision != revision || troopSnapshot.slots.size() != 2U * slotCountPerSide
             || troopSnapshot.slots[sourceIndex].monsterId < 0 || troopTransferRequest.valid ) {
            return false;
        }

        troopTransferRequest = { context, revision, sourceSide, sourceSlot, destinationSide, destinationSlot, true };
        return true;
    }

    TroopTransferRequest takeTroopTransferRequest()
    {
        std::lock_guard<std::mutex> lock( troopMutex );
        TroopTransferRequest request = troopTransferRequest;
        troopTransferRequest = {};
        if ( !request.valid || request.context != getUiContext() || request.context != troopSnapshot.context || request.revision != troopSnapshot.revision ) {
            return {};
        }
        return request;
    }

    UiContextGuard::UiContextGuard( const UiContext context )
        : _previousContext( getUiContext() )
    {
        // The expanded map is a lower-screen-only transient view. Any upper-screen dialog
        // closes it and returns to the ordinary Adventure deck when the dialog exits.
        if ( _previousContext == UiContext::ADVENTURE_MAP_OVERVIEW ) {
            _previousContext = UiContext::ADVENTURE_MAP;
        }
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

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeIsThorViewportControlEnabled( JNIEnv *, jclass )
{
    return fheroes2::thor::isViewportControlEnabled() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeEnqueueThorViewportRequest( JNIEnv *, jclass, const jfloat normalizedX,
                                                                                                         const jfloat normalizedY )
{
    return fheroes2::thor::enqueueViewportRequest( normalizedX, normalizedY ) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeEnqueueThorSelectionRequest( JNIEnv *, jclass, const jint context,
                                                                                                          const jlong revision, const jint kind,
                                                                                                          const jint id )
{
    return fheroes2::thor::enqueueSelectionRequest( static_cast<fheroes2::thor::UiContext>( context ), static_cast<uint64_t>( revision ),
                                                    static_cast<fheroes2::thor::SelectionEntry::Kind>( kind ), id )
               ? JNI_TRUE
               : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeEnqueueThorMarkerInfoRequest( JNIEnv *, jclass, const jint context,
                                                                                                           const jlong revision, const jint kind,
                                                                                                           const jint id )
{
    return fheroes2::thor::enqueueMarkerInfoRequest( static_cast<fheroes2::thor::UiContext>( context ), static_cast<uint64_t>( revision ),
                                                     static_cast<fheroes2::thor::SelectionEntry::Kind>( kind ), id )
               ? JNI_TRUE
               : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL Java_org_fheroes2_GameActivity_nativeEnqueueThorTroopTransferRequest( JNIEnv *, jclass, const jint context, const jlong revision,
                                                                                                            const jint sourceSide, const jint sourceSlot,
                                                                                                            const jint destinationSide, const jint destinationSlot )
{
    return fheroes2::thor::enqueueTroopTransferRequest( static_cast<fheroes2::thor::UiContext>( context ), static_cast<uint64_t>( revision ), sourceSide, sourceSlot,
                                                        destinationSide, destinationSlot )
               ? JNI_TRUE
               : JNI_FALSE;
}

extern "C" JNIEXPORT jobjectArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorSelectionSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    fheroes2::thor::SelectionSnapshot snapshot;
    if ( !fheroes2::thor::getSelectionSnapshot( static_cast<uint64_t>( knownRevision ), snapshot ) ) {
        return nullptr;
    }

    constexpr size_t headerSize = 4;
    constexpr size_t fieldsPerEntry = 9;
    if ( snapshot.entries.size() > ( static_cast<size_t>( std::numeric_limits<jsize>::max() ) - headerSize ) / fieldsPerEntry ) {
        return nullptr;
    }

    jclass stringClass = env->FindClass( "java/lang/String" );
    if ( stringClass == nullptr ) {
        return nullptr;
    }

    const jsize fieldCount = static_cast<jsize>( headerSize + snapshot.entries.size() * fieldsPerEntry );
    jobjectArray fields = env->NewObjectArray( fieldCount, stringClass, nullptr );
    if ( fields == nullptr ) {
        env->DeleteLocalRef( stringClass );
        return nullptr;
    }

    std::vector<std::string> values;
    values.reserve( fieldCount );
    values.emplace_back( std::to_string( snapshot.version ) );
    values.emplace_back( std::to_string( static_cast<int32_t>( snapshot.context ) ) );
    values.emplace_back( std::to_string( snapshot.revision ) );
    values.emplace_back( std::to_string( snapshot.entries.size() ) );
    for ( const fheroes2::thor::SelectionEntry & entry : snapshot.entries ) {
        values.emplace_back( std::to_string( entry.id ) );
        values.emplace_back( entry.name );
        values.emplace_back( entry.detail );
        values.emplace_back( entry.selected ? "1" : "0" );
        values.emplace_back( std::to_string( static_cast<int32_t>( entry.kind ) ) );
        values.emplace_back( std::to_string( entry.x ) );
        values.emplace_back( std::to_string( entry.y ) );
        values.emplace_back( std::to_string( static_cast<int32_t>( entry.relationship ) ) );
        values.emplace_back( entry.selectable ? "1" : "0" );
    }

    for ( jsize index = 0; index < fieldCount; ++index ) {
        jstring value = env->NewStringUTF( values[index].c_str() );
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

extern "C" JNIEXPORT jobjectArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorTroopSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    fheroes2::thor::TroopSnapshot snapshot;
    if ( !fheroes2::thor::getTroopSnapshot( static_cast<uint64_t>( knownRevision ), snapshot ) ) {
        return nullptr;
    }

    constexpr size_t headerSize = 8;
    constexpr size_t fieldsPerSlot = 3;
    if ( snapshot.slots.size() > ( static_cast<size_t>( std::numeric_limits<jsize>::max() ) - headerSize ) / fieldsPerSlot ) {
        return nullptr;
    }

    jclass stringClass = env->FindClass( "java/lang/String" );
    if ( stringClass == nullptr ) {
        return nullptr;
    }

    const jsize fieldCount = static_cast<jsize>( headerSize + snapshot.slots.size() * fieldsPerSlot );
    jobjectArray fields = env->NewObjectArray( fieldCount, stringClass, nullptr );
    if ( fields == nullptr ) {
        env->DeleteLocalRef( stringClass );
        return nullptr;
    }

    std::vector<std::string> values;
    values.reserve( fieldCount );
    values.emplace_back( std::to_string( snapshot.version ) );
    values.emplace_back( std::to_string( static_cast<int32_t>( snapshot.context ) ) );
    values.emplace_back( std::to_string( snapshot.revision ) );
    values.emplace_back( snapshot.leftHero );
    values.emplace_back( snapshot.rightHero );
    values.emplace_back( std::to_string( snapshot.upperSelectedSide ) );
    values.emplace_back( std::to_string( snapshot.upperSelectedSlot ) );
    values.emplace_back( std::to_string( snapshot.slots.size() ) );
    for ( const fheroes2::thor::TroopSlotSnapshot & slot : snapshot.slots ) {
        values.emplace_back( std::to_string( slot.monsterId ) );
        values.emplace_back( slot.name );
        values.emplace_back( std::to_string( slot.count ) );
    }

    for ( jsize index = 0; index < fieldCount; ++index ) {
        jstring value = env->NewStringUTF( values[index].c_str() );
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

extern "C" JNIEXPORT jintArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorTroopVisualSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    fheroes2::thor::TroopSnapshot snapshot;
    if ( !fheroes2::thor::getTroopSnapshot( static_cast<uint64_t>( knownRevision ), snapshot ) ) {
        return nullptr;
    }

    constexpr size_t headerSize = 4;
    constexpr size_t fieldsPerSlot = 3;
    size_t valueCount = headerSize;
    for ( const fheroes2::thor::TroopSlotSnapshot & slot : snapshot.slots ) {
        if ( slot.pixels.size() > static_cast<size_t>( std::numeric_limits<jsize>::max() ) - valueCount - fieldsPerSlot ) {
            return nullptr;
        }
        valueCount += fieldsPerSlot + slot.pixels.size();
    }

    std::vector<jint> values( valueCount );
    values[0] = snapshot.version;
    values[1] = static_cast<jint>( snapshot.context );
    values[2] = static_cast<jint>( snapshot.revision );
    values[3] = static_cast<jint>( snapshot.slots.size() );
    size_t offset = headerSize;
    for ( const fheroes2::thor::TroopSlotSnapshot & slot : snapshot.slots ) {
        values[offset++] = slot.width;
        values[offset++] = slot.height;
        values[offset++] = static_cast<jint>( slot.pixels.size() );
        for ( const uint32_t pixel : slot.pixels ) {
            values[offset++] = static_cast<jint>( pixel );
        }
    }

    jintArray result = env->NewIntArray( static_cast<jsize>( values.size() ) );
    if ( result != nullptr ) {
        env->SetIntArrayRegion( result, 0, static_cast<jsize>( values.size() ), values.data() );
    }
    return result;
}

extern "C" JNIEXPORT jintArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorRadarSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    fheroes2::thor::RadarSnapshot snapshot;
    if ( !fheroes2::thor::getRadarSnapshot( static_cast<uint64_t>( knownRevision ), snapshot ) ) {
        return nullptr;
    }

    constexpr jsize headerSize = 12;
    const size_t pixelCount = snapshot.pixels.size();
    if ( pixelCount > static_cast<size_t>( std::numeric_limits<jsize>::max() - headerSize ) ) {
        return nullptr;
    }

    std::vector<jint> values( headerSize + pixelCount );
    values[0] = snapshot.version;
    values[1] = static_cast<jint>( snapshot.context );
    values[2] = static_cast<jint>( snapshot.revision );
    values[3] = snapshot.width;
    values[4] = snapshot.height;
    values[5] = snapshot.worldWidth;
    values[6] = snapshot.worldHeight;
    values[7] = snapshot.viewportX;
    values[8] = snapshot.viewportY;
    values[9] = snapshot.viewportWidth;
    values[10] = snapshot.viewportHeight;
    values[11] = static_cast<jint>( pixelCount );
    for ( size_t index = 0; index < pixelCount; ++index ) {
        values[headerSize + index] = static_cast<jint>( snapshot.pixels[index] );
    }

    jintArray result = env->NewIntArray( static_cast<jsize>( values.size() ) );
    if ( result != nullptr ) {
        env->SetIntArrayRegion( result, 0, static_cast<jsize>( values.size() ), values.data() );
    }
    return result;
}

extern "C" JNIEXPORT jintArray JNICALL Java_org_fheroes2_GameActivity_nativeGetThorVisualSnapshot( JNIEnv * env, jclass, const jlong knownRevision )
{
    fheroes2::thor::VisualSnapshot snapshot;
    if ( !fheroes2::thor::getVisualSnapshot( static_cast<uint64_t>( knownRevision ), snapshot ) ) {
        return nullptr;
    }

    constexpr jsize headerSize = 6;
    const size_t pixelCount = snapshot.pixels.size();
    if ( pixelCount > static_cast<size_t>( std::numeric_limits<jsize>::max() - headerSize ) ) {
        return nullptr;
    }

    std::vector<jint> values( headerSize + pixelCount );
    values[0] = snapshot.version;
    values[1] = static_cast<jint>( snapshot.context );
    values[2] = static_cast<jint>( snapshot.revision );
    values[3] = snapshot.width;
    values[4] = snapshot.height;
    values[5] = static_cast<jint>( pixelCount );
    for ( size_t index = 0; index < pixelCount; ++index ) {
        values[headerSize + index] = static_cast<jint>( snapshot.pixels[index] );
    }

    jintArray result = env->NewIntArray( static_cast<jsize>( values.size() ) );
    if ( result != nullptr ) {
        env->SetIntArrayRegion( result, 0, static_cast<jsize>( values.size() ), values.data() );
    }
    return result;
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
