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
#include <vector>

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
        SCENARIO_SETUP,
        BATTLE_ONLY_SETUP,
        HIGH_SCORES_STANDARD,
        HIGH_SCORES_CAMPAIGN,
        CAMPAIGN_INTRO,
        SUCCESSION_WARS_CAMPAIGN,
        PRICE_OF_LOYALTY_CAMPAIGN,
        GAME_SETTINGS,
        EDITOR_MAIN_MENU,
        EDITOR_NEW_MAP_MENU,
        EDITOR_MAP_SIZE_SCRATCH,
        EDITOR_MAP_SIZE_RANDOM,
        EDITOR_INTERFACE,
        EDITOR_FILE_OPTIONS,
        EDITOR_SYSTEM_OPTIONS,
        EDITOR_MAP_SPECIFICATIONS,
        EDITOR_MAP_SPEC_PLAYERS,
        EDITOR_MAP_SPEC_VICTORY,
        EDITOR_MAP_SPEC_LOSS,
        EDITOR_TOOLS,
        EDITOR_TOOL_TERRAIN,
        EDITOR_TOOL_LANDSCAPE,
        EDITOR_TOOL_DETAIL,
        EDITOR_TOOL_ADVENTURE,
        EDITOR_TOOL_KINGDOM,
        EDITOR_TOOL_MONSTERS,
        EDITOR_TOOL_STREAMS,
        EDITOR_TOOL_ROADS,
        EDITOR_TOOL_ERASE,
        ADVENTURE_HERO_LIST,
        ADVENTURE_CASTLE_LIST,
        ADVENTURE_MAP_OVERVIEW,
        MENU_FALLBACK,
        HERO_MEETING
    };

    static_assert( static_cast<int32_t>( UiContext::ADVENTURE_MAP_OVERVIEW ) == 43 );
    static_assert( static_cast<int32_t>( UiContext::MENU_FALLBACK ) == 44 );
    static_assert( static_cast<int32_t>( UiContext::HERO_MEETING ) == 45 );

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
        SCENARIO_HANDICAP,
        BATTLE_ONLY_SELECT_ATTACKER,
        BATTLE_ONLY_SELECT_DEFENDER,
        BATTLE_ONLY_PREVIOUS_TERRAIN,
        BATTLE_ONLY_NEXT_TERRAIN,
        BATTLE_ONLY_TOGGLE_DEFENDER_CONTROL,
        BATTLE_ONLY_RESET,
        BATTLE_ONLY_START,
        BATTLE_ONLY_EXIT,
        HIGH_SCORES_VIEW_STANDARD,
        HIGH_SCORES_VIEW_CAMPAIGN,
        HIGH_SCORES_EXIT,
        CAMPAIGN_SELECT_ROLAND,
        CAMPAIGN_SELECT_ARCHIBALD,
        CAMPAIGN_SELECT_PRICE_OF_LOYALTY,
        CAMPAIGN_SELECT_VOYAGE_HOME,
        CAMPAIGN_SELECT_WIZARDS_ISLE,
        CAMPAIGN_SELECT_DESCENDANTS,
        GAME_SETTINGS_LANGUAGE,
        GAME_SETTINGS_GRAPHICS,
        GAME_SETTINGS_AUDIO,
        GAME_SETTINGS_HOT_KEYS,
        GAME_SETTINGS_CURSOR_TYPE,
        GAME_SETTINGS_INTERFACE_TYPE,
        GAME_SETTINGS_TEXT_SUPPORT,
        GAME_SETTINGS_CLOSE,
        MENU_EDITOR,
        EDITOR_NEW_MAP,
        EDITOR_LOAD_MAP,
        EDITOR_EXIT_TO_MAIN_MENU,
        EDITOR_FROM_SCRATCH,
        EDITOR_RANDOM_MAP,
        EDITOR_MAP_SIZE_SMALL,
        EDITOR_MAP_SIZE_MEDIUM,
        EDITOR_MAP_SIZE_LARGE,
        EDITOR_MAP_SIZE_EXTRA_LARGE,
        EDITOR_OPEN_FILE_OPTIONS,
        EDITOR_FILE_NEW_MAP,
        EDITOR_FILE_LOAD_MAP,
        EDITOR_FILE_START_MAP,
        EDITOR_FILE_SAVE_MAP,
        EDITOR_FILE_MAIN_MENU,
        EDITOR_FILE_QUIT,
        EDITOR_FILE_AUTO_PLAYTEST,
        EDITOR_FILE_CANCEL,
        EDITOR_OPEN_SYSTEM_OPTIONS,
        EDITOR_SYSTEM_LANGUAGE,
        EDITOR_SYSTEM_GRAPHICS,
        EDITOR_SYSTEM_AUDIO,
        EDITOR_SYSTEM_HOT_KEYS,
        EDITOR_SYSTEM_ANIMATION,
        EDITOR_SYSTEM_PASSABILITY,
        EDITOR_SYSTEM_INTERFACE_TYPE,
        EDITOR_SYSTEM_CURSOR_TYPE,
        EDITOR_SYSTEM_SCROLL_SPEED,
        EDITOR_SYSTEM_CLOSE,
        EDITOR_OPEN_MAP_SPECIFICATIONS,
        EDITOR_MAP_SPEC_NAME,
        EDITOR_MAP_SPEC_DESCRIPTION,
        EDITOR_MAP_SPEC_PLAYERS,
        EDITOR_MAP_SPEC_DIFFICULTY,
        EDITOR_MAP_SPEC_VICTORY,
        EDITOR_MAP_SPEC_LOSS,
        EDITOR_MAP_SPEC_RUMORS,
        EDITOR_MAP_SPEC_EVENTS,
        EDITOR_MAP_SPEC_LANGUAGE,
        EDITOR_MAP_SPEC_ABOUT,
        EDITOR_MAP_SPEC_OKAY,
        EDITOR_MAP_SPEC_CANCEL,
        EDITOR_MAP_SPEC_PREVIOUS_PLAYER,
        EDITOR_MAP_SPEC_NEXT_PLAYER,
        EDITOR_MAP_SPEC_PLAYER_TYPE,
        EDITOR_MAP_SPEC_PREVIOUS_CONDITION,
        EDITOR_MAP_SPEC_NEXT_CONDITION,
        EDITOR_MAP_SPEC_SELECT_TARGET,
        EDITOR_MAP_SPEC_TOGGLE_STANDARD_VICTORY,
        EDITOR_MAP_SPEC_TOGGLE_AI_VICTORY,
        EDITOR_MAP_SPEC_PREVIOUS_ALLIANCE_PLAYER,
        EDITOR_MAP_SPEC_NEXT_ALLIANCE_PLAYER,
        EDITOR_MAP_SPEC_SWITCH_ALLIANCE,
        EDITOR_MAP_SPEC_DECREASE_VALUE,
        EDITOR_MAP_SPEC_INCREASE_VALUE,
        EDITOR_MAP_SPEC_SUBMENU_BACK,
        EDITOR_OPEN_TOOLS,
        EDITOR_TOOL_TERRAIN,
        EDITOR_TOOL_LANDSCAPE,
        EDITOR_TOOL_DETAIL,
        EDITOR_TOOL_ADVENTURE,
        EDITOR_TOOL_KINGDOM,
        EDITOR_TOOL_MONSTERS,
        EDITOR_TOOL_STREAMS,
        EDITOR_TOOL_ROADS,
        EDITOR_TOOL_ERASE,
        EDITOR_TOOL_MAGNIFY,
        EDITOR_TOOL_UNDO,
        EDITOR_TOOL_REDO,
        EDITOR_TOOL_BACK,
        EDITOR_BRUSH_SMALL,
        EDITOR_BRUSH_MEDIUM,
        EDITOR_BRUSH_LARGE,
        EDITOR_BRUSH_AREA,
        EDITOR_TERRAIN_WATER,
        EDITOR_TERRAIN_GRASS,
        EDITOR_TERRAIN_SNOW,
        EDITOR_TERRAIN_SWAMP,
        EDITOR_TERRAIN_LAVA,
        EDITOR_TERRAIN_DESERT,
        EDITOR_TERRAIN_DIRT,
        EDITOR_TERRAIN_WASTELAND,
        EDITOR_TERRAIN_BEACH,
        EDITOR_LANDSCAPE_MOUNTAINS,
        EDITOR_LANDSCAPE_ROCKS,
        EDITOR_LANDSCAPE_TREES,
        EDITOR_LANDSCAPE_WATER,
        EDITOR_LANDSCAPE_MISC,
        EDITOR_DETAIL_EDIT,
        EDITOR_DETAIL_MOVE,
        EDITOR_DETAIL_COPY,
        EDITOR_ADVENTURE_ARTIFACTS,
        EDITOR_ADVENTURE_DWELLINGS,
        EDITOR_ADVENTURE_MINES,
        EDITOR_ADVENTURE_POWER_UPS,
        EDITOR_ADVENTURE_TREASURES,
        EDITOR_ADVENTURE_WATER,
        EDITOR_ADVENTURE_MISC,
        EDITOR_KINGDOM_HEROES,
        EDITOR_KINGDOM_TOWNS,
        EDITOR_MONSTER_SELECT,
        EDITOR_ERASE_MOUNTAINS,
        EDITOR_ERASE_ROCKS,
        EDITOR_ERASE_TREES,
        EDITOR_ERASE_LANDSCAPE,
        EDITOR_ERASE_ADVENTURE_NON_PICKABLE,
        EDITOR_ERASE_TOWNS,
        EDITOR_ERASE_ADVENTURE_PICKABLE,
        EDITOR_ERASE_MONSTERS,
        EDITOR_ERASE_HEROES,
        EDITOR_ERASE_ROADS,
        EDITOR_ERASE_STREAMS,
        ADVENTURE_OPEN_HERO_LIST,
        ADVENTURE_OPEN_CASTLE_LIST,
        ADVENTURE_SELECTION_BACK,
        ADVENTURE_OPEN_MAP_OVERVIEW,
        ADVENTURE_OVERVIEW_OPEN_HERO_LIST,
        ADVENTURE_OVERVIEW_OPEN_CASTLE_LIST,
        ADVENTURE_OVERVIEW_BACK,
        HERO_MEETING_TRANSFER_TO_RIGHT,
        HERO_MEETING_TRANSFER_TO_LEFT,
        HERO_MEETING_SWAP_ARMIES,
        HERO_MEETING_CLOSE
    };

    static_assert( static_cast<int32_t>( Action::ADVENTURE_OVERVIEW_BACK ) == 217 );
    static_assert( static_cast<int32_t>( Action::HERO_MEETING_CLOSE ) == 221 );

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

    struct RadarSnapshot
    {
        static constexpr int32_t currentVersion = 1;

        int32_t version{ currentVersion };
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        int32_t width{ 0 };
        int32_t height{ 0 };
        int32_t worldWidth{ 0 };
        int32_t worldHeight{ 0 };
        int32_t viewportX{ 0 };
        int32_t viewportY{ 0 };
        int32_t viewportWidth{ 0 };
        int32_t viewportHeight{ 0 };
        std::vector<uint32_t> pixels;
    };

    struct VisualSnapshot
    {
        static constexpr int32_t currentVersion = 1;

        int32_t version{ currentVersion };
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        int32_t width{ 0 };
        int32_t height{ 0 };
        std::vector<uint32_t> pixels;
    };

    struct ViewportRequest
    {
        UiContext context{ UiContext::FALLBACK };
        float normalizedX{ 0.0F };
        float normalizedY{ 0.0F };
        bool valid{ false };
    };

    struct SelectionEntry
    {
        enum class Kind : int32_t
        {
            HERO = 1,
            CASTLE = 2
        };

        enum class Relationship : int32_t
        {
            OWNED = 1,
            ALLIED = 2,
            ENEMY = 3,
            NEUTRAL = 4
        };

        int32_t id{ -1 };
        std::string name;
        std::string detail;
        bool selected{ false };
        Kind kind{ Kind::HERO };
        int32_t x{ -1 };
        int32_t y{ -1 };
        Relationship relationship{ Relationship::OWNED };
        bool selectable{ true };
    };

    struct SelectionSnapshot
    {
        static constexpr int32_t currentVersion = 3;

        int32_t version{ currentVersion };
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        std::vector<SelectionEntry> entries;
    };

    struct SelectionRequest
    {
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        int32_t id{ -1 };
        SelectionEntry::Kind kind{ SelectionEntry::Kind::HERO };
        bool valid{ false };
    };

    struct MarkerInfoRequest
    {
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        int32_t id{ -1 };
        SelectionEntry::Kind kind{ SelectionEntry::Kind::HERO };
        SelectionEntry::Relationship relationship{ SelectionEntry::Relationship::OWNED };
        int32_t ownerColor{ -1 };
        bool clear{ false };
        bool valid{ false };
    };

    struct TroopSlotSnapshot
    {
        int32_t monsterId{ -1 };
        uint32_t count{ 0 };
        std::string name;
        int32_t width{ 0 };
        int32_t height{ 0 };
        std::vector<uint32_t> pixels;
    };

    struct TroopSnapshot
    {
        static constexpr int32_t currentVersion = 1;

        int32_t version{ currentVersion };
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        std::string leftHero;
        std::string rightHero;
        int32_t upperSelectedSide{ -1 };
        int32_t upperSelectedSlot{ -1 };
        std::vector<TroopSlotSnapshot> slots;
    };

    struct TroopMoveRequest
    {
        UiContext context{ UiContext::FALLBACK };
        uint64_t revision{ 0 };
        int32_t sourceSide{ -1 };
        int32_t sourceSlot{ -1 };
        int32_t destinationSide{ -1 };
        int32_t destinationSlot{ -1 };
        bool valid{ false };
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
    bool getRadarSnapshot( uint64_t knownRevision, RadarSnapshot & snapshot );
    void publishRadarSnapshot( RadarSnapshot snapshot );
    bool getVisualSnapshot( uint64_t knownRevision, VisualSnapshot & snapshot );
    void publishVisualSnapshot( VisualSnapshot snapshot );

    // Lower-screen pointer movement is coalesced into one latest-position request. The SDL
    // thread remains the sole owner of viewport changes and rejects requests after context changes.
    bool enqueueViewportRequest( float normalizedX, float normalizedY );
    ViewportRequest takeViewportRequest();
    bool isViewportControlEnabled();
    void setViewportControlEnabled( bool enabled );
    bool getSelectionSnapshot( uint64_t knownRevision, SelectionSnapshot & snapshot );
    void publishSelectionSnapshot( SelectionSnapshot snapshot );
    bool enqueueSelectionRequest( UiContext context, uint64_t revision, SelectionEntry::Kind kind, int32_t id );
    SelectionRequest takeSelectionRequest();
    bool enqueueMarkerInfoRequest( UiContext context, uint64_t revision, SelectionEntry::Kind kind, int32_t id );
    MarkerInfoRequest takeMarkerInfoRequest();
    bool getTroopSnapshot( uint64_t knownRevision, TroopSnapshot & snapshot );
    void publishTroopSnapshot( TroopSnapshot snapshot );
    bool enqueueTroopMoveRequest( UiContext context, uint64_t revision, int32_t sourceSide, int32_t sourceSlot, int32_t destinationSide, int32_t destinationSlot );
    TroopMoveRequest takeTroopMoveRequest();

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
