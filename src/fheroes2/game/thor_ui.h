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

    UiContext getUiContext();
    void setUiContext( UiContext context );

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
