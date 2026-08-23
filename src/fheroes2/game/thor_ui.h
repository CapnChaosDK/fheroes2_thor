/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

#pragma once

#include <cstdint>
#include <string>

namespace fheroes2::thor
{
    enum class UiContext : int32_t
    {
        FALLBACK = 0,
        MAIN_MENU,
        DIALOG,
        ADVENTURE_MAP,
        HERO,
        CASTLE,
        BATTLE,
        NEW_GAME_MENU,
        CAMPAIGN_MENU,
        MULTIPLAYER_MENU,
        HOT_SEAT_MENU,
        LOAD_GAME_MENU,
        SCENARIO_SETUP
    };

    // Stable identifiers shared with the Android command deck. Keep existing values unchanged
    // when adding actions so Java and native builds cannot silently disagree.
    enum class Action : int32_t
    {
        NONE = 0,
        BATTLE_CAST_SPELL,
        BATTLE_SKIP,
        BATTLE_TOGGLE_AUTO_COMBAT,
        BATTLE_QUICK_COMBAT,
        BATTLE_RETREAT,
        BATTLE_SURRENDER,
        BATTLE_OPTIONS,
        BATTLE_TOGGLE_TURN_ORDER,
        ADVENTURE_NEXT_HERO,
        ADVENTURE_NEXT_TOWN,
        ADVENTURE_MOVE,
        ADVENTURE_DEFAULT_ACTION,
        ADVENTURE_CAST_SPELL,
        ADVENTURE_END_TURN,
        ADVENTURE_OPTIONS,
        ADVENTURE_FILE_OPTIONS,
        ADVENTURE_PUZZLE_MAP,
        ADVENTURE_KINGDOM_SUMMARY,
        ADVENTURE_VIEW_WORLD,
        ADVENTURE_DIG_ARTIFACT,
        HERO_PREVIOUS,
        HERO_NEXT,
        HERO_DISMISS,
        HERO_UPGRADE_SELECTED,
        HERO_SPLIT_SELECTED_HALF,
        HERO_SPLIT_SELECTED_ONE,
        HERO_JOIN_SELECTED,
        HERO_SWAP_ARMIES,
        HERO_CLOSE,
        CASTLE_PREVIOUS,
        CASTLE_NEXT,
        CASTLE_WELL,
        CASTLE_MARKETPLACE,
        CASTLE_MAGE_GUILD,
        CASTLE_SHIPYARD,
        CASTLE_THIEVES_GUILD,
        CASTLE_TAVERN,
        CASTLE_CONSTRUCTION,
        CASTLE_TRANSFER_TO_HERO,
        CASTLE_TRANSFER_TO_GARRISON,
        CASTLE_UPGRADE_SELECTED,
        CASTLE_CLOSE,
        MENU_STANDARD_GAME,
        MENU_CAMPAIGN_GAME,
        MENU_MULTIPLAYER_GAME,
        MENU_BATTLE_ONLY,
        MENU_SETTINGS,
        MENU_BACK,
        MENU_ORIGINAL_CAMPAIGN,
        MENU_EXPANSION_CAMPAIGN,
        MENU_HOT_SEAT,
        MENU_HOT_SEAT_2_PLAYERS,
        MENU_HOT_SEAT_3_PLAYERS,
        MENU_HOT_SEAT_4_PLAYERS,
        MENU_HOT_SEAT_5_PLAYERS,
        MENU_HOT_SEAT_6_PLAYERS,
        MENU_LOAD_STANDARD,
        MENU_LOAD_CAMPAIGN,
        MENU_LOAD_HOT_SEAT,
        SCENARIO_SELECT_MAP,
        SCENARIO_DIFFICULTY_EASY,
        SCENARIO_DIFFICULTY_NORMAL,
        SCENARIO_DIFFICULTY_HARD,
        SCENARIO_DIFFICULTY_EXPERT,
        SCENARIO_DIFFICULTY_IMPOSSIBLE,
        SCENARIO_START,
        SCENARIO_PREVIOUS_PLAYER,
        SCENARIO_NEXT_PLAYER,
        SCENARIO_PLAYER_CONTROL,
        SCENARIO_PREVIOUS_FACTION,
        SCENARIO_NEXT_FACTION,
        SCENARIO_HANDICAP
    };

    using ActionMask = uint64_t;

    struct InformationSnapshot
    {
        static constexpr int32_t currentVersion = 1;

        int32_t version{ currentVersion };
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        std::string title;
        std::string category;
        std::string detail;
        std::string date;
        std::string resources;
    };

    constexpr int32_t actionMaskBit( const Action action )
    {
        const int32_t actionId = static_cast<int32_t>( action );
        if ( actionId <= 0 ) {
            return 0;
        }

        // Enabled-action masks are context-local. Existing action IDs retain their original
        // bit positions, while IDs above 63 wrap into a reusable nonzero bit position.
        // Context validation and queue clearing make these cross-context collisions safe.
        constexpr int32_t usableBitCount = 63;
        return actionId <= usableBitCount ? actionId : ( ( actionId - 1 ) % usableBitCount ) + 1;
    }

    constexpr ActionMask actionMask( const Action action )
    {
        return action == Action::NONE ? 0 : ActionMask{ 1 } << actionMaskBit( action );
    }

    UiContext getUiContext();
    void setUiContext( UiContext context );

    // Android produces actions on its main thread and the game consumes them on the SDL thread.
    // Actions are rejected when they do not belong to the currently active context.
    bool enqueueAction( Action action );
    Action takeAction();
    ActionMask getEnabledActions();
    void setEnabledActions( ActionMask actions );
    InformationSnapshot getInformationSnapshot();
    void publishInformationSnapshot( InformationSnapshot snapshot );

    // Restores the previous context when a nested screen or modal dialog closes.
    class UiContextGuard final
    {
    public:
        explicit UiContextGuard( UiContext context );
        ~UiContextGuard();

        UiContextGuard( const UiContextGuard & ) = delete;
        UiContextGuard & operator=( const UiContextGuard & ) = delete;

    private:
        UiContext _previousContext;
    };
}
