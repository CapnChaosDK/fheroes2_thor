/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2021 - 2026                                             *
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

#include "dialog_game_settings.h"

#include <cassert>
#include <functional>
#include <string>
#include <utility>
#include <vector>

#include "dialog.h"
#include "dialog_audio.h"
#include "dialog_graphics_settings.h"
#include "dialog_hotkeys.h"
#include "dialog_language_selection.h"
#include "game_hotkeys.h"
#include "game_language.h"
#include "game_mainmenu_ui.h"
#include "icn.h"
#include "image.h"
#include "localevent.h"
#include "math_base.h"
#include "screen.h"
#include "settings.h"
#include "thor_ui.h"
#include "translations.h"
#include "ui_button.h"
#include "ui_dialog.h"
#include "ui_language.h"
#include "ui_option_item.h"
#include "ui_window.h"

namespace
{
    enum class SelectedWindow : int
    {
        Configuration,
        Language,
        Graphics,
        AudioSettings,
        HotKeys,
        CursorType,
        InterfaceType,
        TextSupportMode,
        UpdateSettings,
        Exit
    };

    const fheroes2::Rect languageRoi{ fheroes2::threeOptionsOffsetX, fheroes2::optionsOffsetY, fheroes2::optionIconSize, fheroes2::optionIconSize };
    const fheroes2::Rect graphicsRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX, fheroes2::optionsOffsetY, fheroes2::optionIconSize,
                                      fheroes2::optionIconSize };
    const fheroes2::Rect audioRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX * 2, fheroes2::optionsOffsetY, fheroes2::optionIconSize,
                                   fheroes2::optionIconSize };

    const fheroes2::Rect hotKeyRoi{ fheroes2::twoOptionsOffsetX, fheroes2::optionsOffsetY + fheroes2::optionsStepY, fheroes2::optionIconSize, fheroes2::optionIconSize };
    const fheroes2::Rect cursorTypeRoi{ fheroes2::twoOptionsOffsetX + fheroes2::twoOptionsStepX, fheroes2::optionsOffsetY + fheroes2::optionsStepY,
                                        fheroes2::optionIconSize, fheroes2::optionIconSize };

    const fheroes2::Rect interfaceTypeRoi{ fheroes2::twoOptionsOffsetX, fheroes2::optionsOffsetY + fheroes2::optionsStepY * 2, fheroes2::optionIconSize,
                                           fheroes2::optionIconSize };
    const fheroes2::Rect textSupportModeRoi{ fheroes2::twoOptionsOffsetX + fheroes2::twoOptionsStepX, fheroes2::optionsOffsetY + fheroes2::optionsStepY * 2,
                                              fheroes2::optionIconSize, fheroes2::optionIconSize };

    std::string getInterfaceTypeName( const InterfaceType interfaceType )
    {
        switch ( interfaceType ) {
        case InterfaceType::GOOD:
            return "Good";
        case InterfaceType::EVIL:
            return "Evil";
        case InterfaceType::DYNAMIC:
            return "Dynamic";
        default:
            assert( 0 );
            return "Unknown";
        }
    }

    void publishThorGameSettings( const std::vector<fheroes2::SupportedLanguage> & supportedLanguages )
    {
        using ThorAction = fheroes2::thor::Action;

        const Settings & conf = Settings::Get();
        const fheroes2::SupportedLanguage currentLanguage = fheroes2::getLanguageFromAbbreviation( conf.getGameLanguage() );
        const fheroes2::LanguageSwitcher languageSwitcher( currentLanguage );

        fheroes2::thor::InformationSnapshot snapshot;
        snapshot.context = fheroes2::thor::UiContext::GAME_SETTINGS;
        snapshot.title = "Language: " + std::string( fheroes2::getLanguageName( currentLanguage ) );
        snapshot.category = "CURRENT SETTINGS";
        snapshot.detail = std::string( "Cursor: " ) + ( conf.isMonochromeCursorEnabled() ? "Monochrome" : "Color" )
                          + " | Interface: " + getInterfaceTypeName( conf.getInterfaceType() );
        snapshot.date = std::to_string( supportedLanguages.size() ) + ( supportedLanguages.size() == 1 ? " LANGUAGE" : " LANGUAGES" );
        snapshot.resources = std::string( "Text Support: " ) + ( conf.isTextSupportModeEnabled() ? "On" : "Off" );
        fheroes2::thor::publishInformationSnapshot( std::move( snapshot ) );

        fheroes2::thor::ActionMask enabledActions = fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_GRAPHICS )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_AUDIO )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_HOT_KEYS )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_CURSOR_TYPE )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_INTERFACE_TYPE )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_TEXT_SUPPORT )
                                                    | fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_CLOSE );
        if ( supportedLanguages.size() > 1 ) {
            enabledActions |= fheroes2::thor::actionMask( ThorAction::GAME_SETTINGS_LANGUAGE );
        }
        fheroes2::thor::setEnabledActions( enabledActions );
    }

    SelectedWindow showConfigurationWindow( const std::vector<fheroes2::SupportedLanguage> & supportedLanguages )
    {
        using ThorAction = fheroes2::thor::Action;

        fheroes2::Display & display = fheroes2::Display::instance();

        fheroes2::StandardWindow background( 289, fheroes2::optionsStepY * 3 + 52, true, display );

        const fheroes2::Rect windowRoi = background.activeArea();

        const Settings & conf = Settings::Get();
        const bool isEvilInterface = conf.isEvilInterfaceEnabled();

        fheroes2::Button buttonOk;
        const int buttonOkIcnId = isEvilInterface ? ICN::BUTTON_SMALL_OKAY_EVIL : ICN::BUTTON_SMALL_OKAY_GOOD;
        background.renderButton( buttonOk, buttonOkIcnId, 0, 1, { 0, 11 }, fheroes2::StandardWindow::Padding::BOTTOM_CENTER );

        fheroes2::ImageRestorer emptyDialogRestorer( display, windowRoi.x, windowRoi.y, windowRoi.width, windowRoi.height );

        const fheroes2::Rect windowLanguageRoi( languageRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowGraphicsRoi( graphicsRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowAudioRoi( audioRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowHotKeyRoi( hotKeyRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowCursorTypeRoi( cursorTypeRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowInterfaceTypeRoi( interfaceTypeRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowTextSupportModeRoi( textSupportModeRoi + windowRoi.getPosition() );

        const auto drawOptions = [&conf, &windowLanguageRoi, &windowGraphicsRoi, &windowAudioRoi, &windowHotKeyRoi, &windowCursorTypeRoi, &windowInterfaceTypeRoi,
                                  &windowTextSupportModeRoi]() {
            drawLanguage( windowLanguageRoi, conf.getGameLanguage(), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawGraphics( windowGraphicsRoi, fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawAudioOptions( windowAudioRoi, fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawHotKeyOptions( windowHotKeyRoi, fheroes2::UiOptionTextWidth::TWO_ELEMENTS_ROW );
            drawCursorType( windowCursorTypeRoi, conf.isMonochromeCursorEnabled(), fheroes2::UiOptionTextWidth::TWO_ELEMENTS_ROW );
            drawInterfaceType( windowInterfaceTypeRoi, conf.getInterfaceType(), fheroes2::UiOptionTextWidth::TWO_ELEMENTS_ROW );
            drawTextSupportModeOptions( windowTextSupportModeRoi, conf.isTextSupportModeEnabled(), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
        };

        drawOptions();

        display.render( background.totalArea() );
        publishThorGameSettings( supportedLanguages );

        bool isTextSupportModeEnabled = conf.isTextSupportModeEnabled();

        LocalEvent & le = LocalEvent::Get();
        while ( le.HandleEvents() ) {
            buttonOk.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonOk.area() ) );

            const ThorAction requestedThorAction = fheroes2::thor::takeAction();
            if ( requestedThorAction != ThorAction::NONE ) {
                // Consume one lower-screen request and reject queued duplicates until this panel is rebuilt.
                fheroes2::thor::setEnabledActions( 0 );
            }

            if ( requestedThorAction == ThorAction::GAME_SETTINGS_CLOSE || le.MouseClickLeft( buttonOk.area() ) || Game::HotKeyCloseWindow() ) {
                break;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_LANGUAGE || le.MouseClickLeft( windowLanguageRoi ) ) {
                return SelectedWindow::Language;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_GRAPHICS || le.MouseClickLeft( windowGraphicsRoi ) ) {
                return SelectedWindow::Graphics;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_AUDIO || le.MouseClickLeft( windowAudioRoi ) ) {
                return SelectedWindow::AudioSettings;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_HOT_KEYS || le.MouseClickLeft( windowHotKeyRoi ) ) {
                return SelectedWindow::HotKeys;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_CURSOR_TYPE || le.MouseClickLeft( windowCursorTypeRoi ) ) {
                return SelectedWindow::CursorType;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_INTERFACE_TYPE || le.MouseClickLeft( windowInterfaceTypeRoi ) ) {
                return SelectedWindow::InterfaceType;
            }
            if ( requestedThorAction == ThorAction::GAME_SETTINGS_TEXT_SUPPORT || le.MouseClickLeft( windowTextSupportModeRoi ) ) {
                return SelectedWindow::TextSupportMode;
            }

            if ( le.isMouseRightButtonPressedInArea( windowLanguageRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Select Game Language" ), _( "Change the language of the game." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowGraphicsRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Graphics" ), _( "Change the graphics settings of the game." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowAudioRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Audio" ), _( "Change the audio settings of the game." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowHotKeyRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Hot Keys" ), _( "Check and configure all the hot keys present in the game." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowCursorTypeRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Mouse Cursor" ), _( "Toggle colored cursor on or off. This is only an aesthetic choice." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowInterfaceTypeRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Interface Type" ), _( "Toggle the type of interface you want to use." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowTextSupportModeRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Text Support" ), _( "Toggle text support mode to output extra information about windows and events in the game." ),
                                                   0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( buttonOk.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Okay" ), _( "Exit this menu." ), 0 );
            }

            // Text support mode can be toggled using a global hotkey, we need to properly reflect this change in the UI
            if ( isTextSupportModeEnabled != conf.isTextSupportModeEnabled() ) {
                isTextSupportModeEnabled = conf.isTextSupportModeEnabled();

                emptyDialogRestorer.restore();
                drawOptions();

                display.render( emptyDialogRestorer.rect() );
            }

            // Help dialogs temporarily clear the semantic action mask while using Dialog context.
            // Republish the current panel after any such nested dialog closes.
            publishThorGameSettings( supportedLanguages );
        }

        return SelectedWindow::Exit;
    }
}

namespace fheroes2
{
    void openGameSettings()
    {
        const thor::UiContextGuard thorContextGuard( thor::UiContext::GAME_SETTINGS );

        drawMainMenuScreen();

        Display & display = Display::instance();

        // First we need to render the whole screen to update the rendered background image when this dialog is called from the menu.
        display.updateNextRenderRoi( { 0, 0, display.width(), display.height() } );

        Settings & conf = Settings::Get();
        const std::vector<SupportedLanguage> supportedLanguages = getSupportedLanguages();

        bool saveConfiguration = false;

        SelectedWindow windowType = SelectedWindow::Configuration;
        while ( windowType != SelectedWindow::Exit ) {
            switch ( windowType ) {
            case SelectedWindow::Configuration:
                windowType = showConfigurationWindow( supportedLanguages );
                break;
            case SelectedWindow::Language: {
                const thor::UiContextGuard dialogContext( thor::UiContext::DIALOG );

                if ( supportedLanguages.size() > 1 ) {
                    selectLanguage( supportedLanguages, getLanguageFromAbbreviation( conf.getGameLanguage() ), true );
                }
                else {
                    assert( supportedLanguages.front() == SupportedLanguage::English );

                    conf.setGameLanguage( getLanguageAbbreviation( SupportedLanguage::English ) );

                    showStandardTextMessage( "Attention", "Your version of Heroes of Might and Magic II does not support any other languages than English.", Dialog::OK );
                }

                windowType = SelectedWindow::UpdateSettings;
                break;
            }
            case SelectedWindow::Graphics: {
                const thor::UiContextGuard dialogContext( thor::UiContext::DIALOG );
                saveConfiguration |= openGraphicsSettingsDialog( drawMainMenuScreen );
                windowType = SelectedWindow::Configuration;
                break;
            }
            case SelectedWindow::AudioSettings: {
                const thor::UiContextGuard dialogContext( thor::UiContext::DIALOG );
                saveConfiguration |= Dialog::openAudioSettingsDialog( false );
                windowType = SelectedWindow::Configuration;
                break;
            }
            case SelectedWindow::HotKeys: {
                const thor::UiContextGuard dialogContext( thor::UiContext::DIALOG );
                openHotkeysDialog();
                windowType = SelectedWindow::Configuration;
                break;
            }
            case SelectedWindow::CursorType:
                conf.setMonochromeCursor( !conf.isMonochromeCursorEnabled() );
                windowType = SelectedWindow::UpdateSettings;
                break;
            case SelectedWindow::TextSupportMode:
                conf.setTextSupportMode( !conf.isTextSupportModeEnabled() );
                windowType = SelectedWindow::UpdateSettings;
                break;
            case SelectedWindow::InterfaceType:
                conf.switchToNextInterfaceType();
                windowType = SelectedWindow::UpdateSettings;
                break;
            case SelectedWindow::UpdateSettings:
                saveConfiguration = true;
                windowType = SelectedWindow::Configuration;
                break;
            default:
                return;
            }
        }

        if ( saveConfiguration ) {
            conf.Save( Settings::configFileName );
        }
    }
}
