/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2019 - 2025                                             *
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

#include "game.h" // IWYU pragma: associated

#include <cstddef>
#include <cstdint>
#include <ostream>
#include <string>

#include "audio.h"
#include "audio_manager.h"
#include "cursor.h"
#include "dialog.h"
#include "dir.h"
#include "game_hotkeys.h"
#include "game_io.h"
#include "game_mainmenu_ui.h"
#include "game_mode.h"
#include "icn.h"
#include "image.h"
#include "localevent.h"
#include "logging.h"
#include "mus.h"
#include "screen.h"
#include "settings.h"
#include "thor_ui.h"
#include "translations.h"
#include "ui_button.h"
#include "ui_dialog.h"
#include "ui_tool.h"
#include "ui_window.h"

namespace
{
    void outputLoadGameInTextSupportMode()
    {
        START_TEXT_SUPPORT_MODE

        COUT( "Load Game\n" )

        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::MAIN_MENU_STANDARD ) << " to choose Standard Game." )
        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::MAIN_MENU_CAMPAIGN ) << " to choose Campaign Game." )
        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::MAIN_MENU_HOTSEAT ) << " to choose Hot Seat Game." )
        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::DEFAULT_CANCEL ) << " to go back to Main Menu." )
    }
}

fheroes2::GameMode Game::LoadCampaign()
{
    Settings::Get().SetGameType( Game::TYPE_CAMPAIGN );
    return DisplayLoadGameDialog();
}

fheroes2::GameMode Game::LoadHotseat()
{
    Settings::Get().SetGameType( Game::TYPE_HOTSEAT );
    return DisplayLoadGameDialog();
}

fheroes2::GameMode Game::LoadGame()
{
    outputLoadGameInTextSupportMode();

    // Stop all sounds, but not the music
    AudioManager::stopSounds();

    AudioManager::PlayMusicAsync( MUS::MAINMENU, Music::PlaybackMode::RESUME_AND_PLAY_INFINITE );

    // Setup cursor
    const CursorRestorer cursorRestorer( true, Cursor::POINTER );

    fheroes2::drawMainMenuScreen();

    fheroes2::ButtonGroup gameModeButtons;
    const int menuButtonsIcnIndex = Settings::Get().isEvilInterfaceEnabled() ? ICN::BUTTONS_NEW_GAME_MENU_EVIL : ICN::BUTTONS_NEW_GAME_MENU_GOOD;
    for ( int32_t i = 0; i < 2; ++i ) {
        gameModeButtons.createButton( 0, 0, menuButtonsIcnIndex, i * 2, i * 2 + 1, i );
    }
    gameModeButtons.createButton( 0, 0, menuButtonsIcnIndex, 12, 13, 2 );

    fheroes2::ButtonBase & buttonStandardGame = gameModeButtons.button( 0 );
    fheroes2::ButtonBase & buttonCampaignGame = gameModeButtons.button( 1 );
    fheroes2::ButtonBase & buttonHotSeat = gameModeButtons.button( 2 );

    const int32_t spaceBetweenButtons = 10;

    fheroes2::StandardWindow background( gameModeButtons, true, buttonStandardGame.area().height * 3 + spaceBetweenButtons * 3 );

    // Make corners like in the original game.
    background.applyGemDecoratedCorners();

    fheroes2::Display & display = fheroes2::Display::instance();

    const bool hasStandardSaves = !ListFiles::IsEmpty( GetSaveDir(), GetSaveFileExtension( Game::TYPE_STANDARD ) );
    const bool hasCampaignSaves = isSuccessionWarsCampaignPresent() && !ListFiles::IsEmpty( GetSaveDir(), GetSaveFileExtension( Game::TYPE_CAMPAIGN ) );
    const bool hasHotSeatSaves = !ListFiles::IsEmpty( GetSaveDir(), GetSaveFileExtension( Game::TYPE_HOTSEAT ) );

    if ( !hasStandardSaves ) {
        buttonStandardGame.disable();
    }
    if ( !hasCampaignSaves ) {
        buttonCampaignGame.disable();
    }
    if ( !hasHotSeatSaves ) {
        buttonHotSeat.disable();
    }

    background.renderSymmetricButtons( gameModeButtons, 0, true );

    // Add the cancel button at the bottom of the dialog.
    fheroes2::Button buttonCancel( buttonStandardGame.area().x,
                                   background.activeArea().y * 2 + background.activeArea().height - buttonStandardGame.area().y - buttonStandardGame.area().height,
                                   menuButtonsIcnIndex, 10, 11 );
    buttonCancel.draw();
    buttonCancel.drawShadow( display );

    using ThorAction = fheroes2::thor::Action;
    fheroes2::thor::ActionMask enabledThorActions = fheroes2::thor::actionMask( ThorAction::MENU_BACK );
    if ( hasStandardSaves ) {
        enabledThorActions |= fheroes2::thor::actionMask( ThorAction::MENU_LOAD_STANDARD );
    }
    if ( hasCampaignSaves ) {
        enabledThorActions |= fheroes2::thor::actionMask( ThorAction::MENU_LOAD_CAMPAIGN );
    }
    if ( hasHotSeatSaves ) {
        enabledThorActions |= fheroes2::thor::actionMask( ThorAction::MENU_LOAD_HOT_SEAT );
    }

    fheroes2::thor::setUiContext( fheroes2::thor::UiContext::LOAD_GAME_MENU );
    fheroes2::thor::setEnabledActions( enabledThorActions );

    fheroes2::validateFadeInAndRender();

    LocalEvent & le = LocalEvent::Get();

    while ( le.HandleEvents() ) {
        const ThorAction requestedThorAction = fheroes2::thor::takeAction();
        gameModeButtons.drawOnState( le );

        if ( buttonStandardGame.isEnabled()
             && ( requestedThorAction == ThorAction::MENU_LOAD_STANDARD || le.MouseClickLeft( buttonStandardGame.area() )
                  || HotKeyPressEvent( HotKeyEvent::MAIN_MENU_STANDARD ) ) ) {
            return fheroes2::GameMode::LOAD_STANDARD;
        }
        if ( buttonCampaignGame.isEnabled()
             && ( requestedThorAction == ThorAction::MENU_LOAD_CAMPAIGN || le.MouseClickLeft( buttonCampaignGame.area() )
                  || HotKeyPressEvent( HotKeyEvent::MAIN_MENU_CAMPAIGN ) ) ) {
            return fheroes2::GameMode::LOAD_CAMPAIGN;
        }
        if ( buttonHotSeat.isEnabled()
             && ( requestedThorAction == ThorAction::MENU_LOAD_HOT_SEAT || le.MouseClickLeft( buttonHotSeat.area() )
                  || HotKeyPressEvent( HotKeyEvent::MAIN_MENU_HOTSEAT ) ) ) {
            return fheroes2::GameMode::LOAD_HOT_SEAT;
        }

        if ( le.isMouseRightButtonPressedInArea( buttonStandardGame.area() ) ) {
            fheroes2::showStandardTextMessage( _( "Standard Game" ), _( "A single player game playing out a single map." ), Dialog::ZERO );
        }
        else if ( le.isMouseRightButtonPressedInArea( buttonCampaignGame.area() ) ) {
            fheroes2::showStandardTextMessage( _( "Campaign Game" ), _( "A single player game playing through a series of maps." ), Dialog::ZERO );
        }
        else if ( le.isMouseRightButtonPressedInArea( buttonHotSeat.area() ) ) {
            fheroes2::showStandardTextMessage(
                _( "Hot Seat" ), _( "Play a Hot Seat game, where 2 to 6 players play around the same computer, switching into the 'Hot Seat' when it is their turn." ),
                Dialog::ZERO );
        }

        buttonCancel.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonCancel.area() ) );

        if ( requestedThorAction == ThorAction::MENU_BACK || le.MouseClickLeft( buttonCancel.area() ) || HotKeyPressEvent( HotKeyEvent::DEFAULT_CANCEL ) ) {
            return fheroes2::GameMode::MAIN_MENU;
        }

        if ( le.isMouseRightButtonPressedInArea( buttonCancel.area() ) ) {
            fheroes2::showStandardTextMessage( _( "Cancel" ), _( "Cancel back to the main menu." ), Dialog::ZERO );
        }
    }

    return fheroes2::GameMode::MAIN_MENU;
}

fheroes2::GameMode Game::LoadStandard()
{
    Settings::Get().SetGameType( Game::TYPE_STANDARD );
    return DisplayLoadGameDialog();
}

fheroes2::GameMode Game::DisplayLoadGameDialog()
{
    // Stop all sounds, but not the music
    AudioManager::stopSounds();

    AudioManager::PlayMusicAsync( MUS::MAINMENU, Music::PlaybackMode::RESUME_AND_PLAY_INFINITE );

    // setup cursor
    const CursorRestorer cursorRestorer( true, Cursor::POINTER );

    // image background
    fheroes2::drawMainMenuScreen();

    fheroes2::validateFadeInAndRender();

    const std::string file = Dialog::SelectFileLoad();
    if ( file.empty() ) {
        return fheroes2::GameMode::LOAD_GAME;
    }

    const fheroes2::GameMode returnValue = Game::Load( file );
    if ( returnValue == fheroes2::GameMode::CANCEL ) {
        return fheroes2::GameMode::LOAD_GAME;
    }

    // We are loading a game so fade-out main menu screen.
    fheroes2::fadeOutDisplay();

    setDisplayFadeIn();

    return returnValue;
}
