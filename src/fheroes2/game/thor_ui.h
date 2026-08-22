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
        BATTLE
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
        ADVENTURE_DIG_ARTIFACT
    };

    using ActionMask = uint64_t;

    constexpr ActionMask actionMask( const Action action )
    {
        return ActionMask{ 1 } << static_cast<int32_t>( action );
    }

    UiContext getUiContext();
    void setUiContext( UiContext context );

    // Android produces actions on its main thread and the game consumes them on the SDL thread.
    // Actions are rejected when they do not belong to the currently active context.
    bool enqueueAction( Action action );
    Action takeAction();
    ActionMask getEnabledActions();
    void setEnabledActions( ActionMask actions );

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
