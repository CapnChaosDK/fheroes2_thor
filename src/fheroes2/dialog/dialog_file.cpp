/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2019 - 2026                                             *
 *                                                                         *
 *   Free Heroes2 Engine: http://sourceforge.net/projects/fheroes2         *
 *   Copyright (C) 2009 by Andrey Afletdinov <fheroes2@gmail.com>          *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

#include "cursor.h"
#include "dialog.h" // IWYU pragma: associated
#include "game_exit.h"
#include "game_hotkeys.h"
#include "game_interface.h"
#include "game_io.h"
#include "game_mode.h"
#include "icn.h"
#include "localevent.h"
#include "screen.h"
#include "settings.h"
#include "thor_ui.h"
#include "translations.h"
#include "ui_button.h"
#include "ui_dialog.h"
#include "ui_window.h"

namespace
{
    fheroes2::GameMode selectFileOption()
    {
        // setup cursor
        const CursorRestorer cursorRestorer( true, Cursor::POINTER );

        fheroes2::Display & display = fheroes2::Display::instance();

        const auto & config = Settings::Get();
        const bool isEvilInterface = config.isEvilInterfaceEnabled();
        const int bigButtonsICN = isEvilInterface ? ICN::BUTTONS_FILE_DIALOG_EVIL : ICN::BUTTONS_FILE_DIALOG_GOOD;
        fheroes2::ButtonGroup optionButtons( bigButtonsICN );
        fheroes2::StandardWindow background( optionButtons, false, 0, display );

        const fheroes2::ButtonBase & newGameButton = optionButtons.button( 0 );
        const fheroes2::ButtonBase & loadGameButton = optionButtons.button( 1 );
        fheroes2::ButtonBase & restartGameButton = optionButtons.button( 2 );
        const fheroes2::ButtonBase & saveGameButton = optionButtons.button( 3 );
        const fheroes2::ButtonBase & quickSaveButton = optionButtons.button( 4 );
        const fheroes2::ButtonBase & quitButton = optionButtons.button( 5 );

        // For now this button is disabled.
        restartGameButton.disable();

        background.renderSymmetricButtons( optionButtons, 0, false );

        fheroes2::Button buttonCancel;

        background.renderButton( buttonCancel, isEvilInterface ? ICN::BUTTON_SMALL_CANCEL_EVIL : ICN::BUTTON_SMALL_CANCEL_GOOD, 0, 1, { 0, 11 },
                                 fheroes2::StandardWindow::Padding::BOTTOM_CENTER );

        display.render( background.totalArea() );

        fheroes2::GameMode result = fheroes2::GameMode::QUIT_GAME;

        LocalEvent & le = LocalEvent::Get();

        using ThorAction = fheroes2::thor::Action;
        const fheroes2::thor::ActionMask thorActions = fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_NEW_GAME )
                                                          | fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_LOAD_GAME )
                                                          | fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_SAVE_GAME )
                                                          | fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_QUICK_SAVE )
                                                          | fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_QUIT )
                                                          | fheroes2::thor::actionMask( ThorAction::ADVENTURE_FILE_CANCEL );

        // dialog menu loop
        while ( true ) {
            fheroes2::thor::setUiContext( fheroes2::thor::UiContext::ADVENTURE_FILE_OPTIONS );
            fheroes2::thor::setEnabledActions( thorActions );

            if ( !le.HandleEvents() ) {
                break;
            }

            optionButtons.drawOnState( le );
            buttonCancel.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonCancel.area() ) );

            const ThorAction requestedThorAction = fheroes2::thor::takeAction();
            if ( requestedThorAction != ThorAction::NONE ) {
                // Reject rapid follow-up taps until this operation or its nested dialog has completed.
                fheroes2::thor::setEnabledActions( 0 );
            }

            if ( requestedThorAction == ThorAction::ADVENTURE_FILE_NEW_GAME || le.MouseClickLeft( newGameButton.area() )
                 || Game::HotKeyPressEvent( Game::HotKeyEvent::MAIN_MENU_NEW_GAME ) ) {
                if ( Interface::AdventureMap::Get().EventNewGame() == fheroes2::GameMode::NEW_GAME ) {
                    result = fheroes2::GameMode::NEW_GAME;
                    break;
                }
            }
            else if ( requestedThorAction == ThorAction::ADVENTURE_FILE_LOAD_GAME || le.MouseClickLeft( loadGameButton.area() )
                      || Game::HotKeyPressEvent( Game::HotKeyEvent::MAIN_MENU_LOAD_GAME ) ) {
                if ( Interface::AdventureMap::Get().EventLoadGame() == fheroes2::GameMode::LOAD_GAME ) {
                    result = fheroes2::GameMode::LOAD_GAME;
                    break;
                }
            }
            else if ( restartGameButton.isEnabled()
                      && ( requestedThorAction == ThorAction::ADVENTURE_FILE_RESTART_GAME || le.MouseClickLeft( restartGameButton.area() ) ) ) {
                // TODO: restart the campaign here.
                fheroes2::showStandardTextMessage( _( "Restart Game" ), "This option is under construction.", Dialog::OK );
                result = fheroes2::GameMode::CANCEL;
                break;
            }
            else if ( requestedThorAction == ThorAction::ADVENTURE_FILE_SAVE_GAME || le.MouseClickLeft( saveGameButton.area() )
                      || Game::HotKeyPressEvent( Game::HotKeyEvent::WORLD_SAVE_GAME ) ) {
                // Special case: since we show a window about file saving we don't want to display the current dialog anymore.
                background.hideWindow();

                const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
                return Interface::AdventureMap::Get().EventSaveGame();
            }
            else if ( requestedThorAction == ThorAction::ADVENTURE_FILE_QUICK_SAVE || le.MouseClickLeft( quickSaveButton.area() ) ) {
                if ( !Game::QuickSave() ) {
                    fheroes2::showStandardTextMessage( "", _( "There was an issue during saving." ), Dialog::OK );
                }

                result = fheroes2::GameMode::CANCEL;
                break;
            }

            if ( requestedThorAction == ThorAction::ADVENTURE_FILE_QUIT || le.MouseClickLeft( quitButton.area() )
                 || Game::HotKeyPressEvent( Game::HotKeyEvent::GLOBAL_APP_QUIT ) ) {
                if ( Game::processExitEvent() == fheroes2::GameMode::QUIT_GAME ) {
                    result = fheroes2::GameMode::QUIT_GAME;
                    break;
                }
            }
            else if ( requestedThorAction == ThorAction::ADVENTURE_FILE_CANCEL || le.MouseClickLeft( buttonCancel.area() ) || Game::HotKeyCloseWindow() ) {
                result = fheroes2::GameMode::CANCEL;
                break;
            }

            if ( le.isMouseRightButtonPressedInArea( newGameButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "New Game" ), _( "Start a single or multi-player game." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( loadGameButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Load Game" ), _( "Load a previously saved game." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( restartGameButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Restart Game" ), _( "Restart the scenario." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( saveGameButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Save Game" ), _( "Save the current game." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( quickSaveButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Quick Save" ), _( "Save the current game without name selection." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( quitButton.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Quit" ), _( "Quit out of Heroes of Might and Magic II." ), Dialog::ZERO );
            }
            else if ( le.isMouseRightButtonPressedInArea( buttonCancel.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Cancel" ), _( "Exit this menu without doing anything." ), Dialog::ZERO );
            }
        }

        return result;
    }
}

fheroes2::GameMode Dialog::FileOptions()
{
    const fheroes2::GameMode result = selectFileOption();
    return result;
}
