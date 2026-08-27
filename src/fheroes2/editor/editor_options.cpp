/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2024 - 2026                                             *
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

#include "editor_options.h"

#include <cassert>
#include <cstdint>
#include <functional>
#include <string>
#include <utility>
#include <vector>

#include "cursor.h"
#include "dialog.h"
#include "dialog_audio.h"
#include "dialog_graphics_settings.h"
#include "dialog_hotkeys.h"
#include "dialog_language_selection.h"
#include "editor_interface.h"
#include "game_assets.h"
#include "game_hotkeys.h"
#include "game_language.h"
#include "icn.h"
#include "interface_base.h"
#include "localevent.h"
#include "math_base.h"
#include "render_processor.h"
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
    enum class DialogAction : uint8_t
    {
        Configuration,
        Language,
        Graphics,
        AudioSettings,
        HotKeys,
        Animation,
        Passabiility,
        UpdateSettings,
        InterfaceType,
        CursorType,
        UpdateScrollSpeed,
        IncreaseScrollSpeed,
        DecreaseScrollSpeed,
        Close
    };

    const fheroes2::Rect languageRoi{ fheroes2::threeOptionsOffsetX, fheroes2::optionsOffsetY, fheroes2::optionIconSize, fheroes2::optionIconSize };
    const fheroes2::Rect graphicsRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX, fheroes2::optionsOffsetY, fheroes2::optionIconSize,
                                      fheroes2::optionIconSize };
    const fheroes2::Rect audioRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX * 2, fheroes2::optionsOffsetY, fheroes2::optionIconSize,
                                   fheroes2::optionIconSize };

    const fheroes2::Rect hotKeyRoi{ fheroes2::threeOptionsOffsetX, fheroes2::optionsOffsetY + fheroes2::optionsStepY, fheroes2::optionIconSize,
                                    fheroes2::optionIconSize };
    const fheroes2::Rect animationRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX, fheroes2::optionsOffsetY + fheroes2::optionsStepY,
                                       fheroes2::optionIconSize, fheroes2::optionIconSize };
    const fheroes2::Rect passabilityRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX * 2, fheroes2::optionsOffsetY + fheroes2::optionsStepY,
                                         fheroes2::optionIconSize, fheroes2::optionIconSize };

    const fheroes2::Rect interfaceTypeRoi{ fheroes2::threeOptionsOffsetX, fheroes2::optionsOffsetY + fheroes2::optionsStepY * 2, fheroes2::optionIconSize,
                                           fheroes2::optionIconSize };
    const fheroes2::Rect cursorTypeRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX, fheroes2::optionsOffsetY + fheroes2::optionsStepY * 2,
                                        fheroes2::optionIconSize, fheroes2::optionIconSize };
    const fheroes2::Rect scrollSpeedRoi{ fheroes2::threeOptionsOffsetX + fheroes2::threeOptionsStepX * 2, fheroes2::optionsOffsetY + fheroes2::optionsStepY * 2,
                                         fheroes2::optionIconSize, fheroes2::optionIconSize };

    void drawAnimationOptions( const fheroes2::Rect & optionRoi )
    {
        if ( Settings::Get().isEditorAnimationEnabled() ) {
            fheroes2::drawOption( optionRoi, Assets::getImage( ICN::ESPANEL, 1 ), _( "Animation" ), _( "On" ), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
        }
        else {
            fheroes2::drawOption( optionRoi, Assets::getImage( ICN::ESPANEL, 0 ), _( "Animation" ), _( "Off" ), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
        }
    }

    void drawPassabilityOptions( const fheroes2::Rect & optionRoi )
    {
        if ( Settings::Get().isEditorPassabilityEnabled() ) {
            fheroes2::drawOption( optionRoi, Assets::getImage( ICN::ESPANEL, 5 ), _( "Passability" ), _( "Show" ), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
        }
        else {
            fheroes2::drawOption( optionRoi, Assets::getImage( ICN::ESPANEL, 4 ), _( "Passability" ), _( "Hide" ), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
        }
    }

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

    std::string getScrollSpeedName( const int speed )
    {
        switch ( speed ) {
        case SCROLL_SPEED_NONE:
            return "Off";
        case SCROLL_SPEED_SLOW:
            return "Slow";
        case SCROLL_SPEED_NORMAL:
            return "Normal";
        case SCROLL_SPEED_FAST:
            return "Fast";
        case SCROLL_SPEED_VERY_FAST:
            return "Very Fast";
        default:
            assert( 0 );
            return "Unknown";
        }
    }

    void publishThorEditorSettings( const std::vector<fheroes2::SupportedLanguage> & supportedLanguages )
    {
        using ThorAction = fheroes2::thor::Action;

        const Settings & conf = Settings::Get();
        const fheroes2::SupportedLanguage currentLanguage = fheroes2::getLanguageFromAbbreviation( conf.getGameLanguage() );
        const fheroes2::LanguageSwitcher languageSwitcher( currentLanguage );

        fheroes2::thor::InformationSnapshot snapshot;
        snapshot.context = fheroes2::thor::UiContext::EDITOR_SYSTEM_OPTIONS;
        snapshot.title = std::string( "Animation: " ) + ( conf.isEditorAnimationEnabled() ? "On" : "Off" )
                         + " | Passability: " + ( conf.isEditorPassabilityEnabled() ? "Show" : "Hide" );
        snapshot.category = "EDITOR SETTINGS";
        snapshot.detail = std::string( "Cursor: " ) + ( conf.isMonochromeCursorEnabled() ? "Monochrome" : "Color" )
                          + " | Interface: " + getInterfaceTypeName( conf.getInterfaceType() );
        snapshot.date = "Scroll: " + getScrollSpeedName( conf.ScrollSpeed() );
        snapshot.resources = "Language: " + std::string( fheroes2::getLanguageName( currentLanguage ) );
        fheroes2::thor::publishInformationSnapshot( std::move( snapshot ) );

        fheroes2::thor::ActionMask enabledActions = fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_GRAPHICS )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_AUDIO )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_HOT_KEYS )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_ANIMATION )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_PASSABILITY )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_INTERFACE_TYPE )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_CURSOR_TYPE )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_SCROLL_SPEED )
                                                    | fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_CLOSE );
        if ( supportedLanguages.size() > 1 ) {
            enabledActions |= fheroes2::thor::actionMask( ThorAction::EDITOR_SYSTEM_LANGUAGE );
        }
        fheroes2::thor::setEnabledActions( enabledActions );
    }

    DialogAction openEditorOptionsDialog( const std::vector<fheroes2::SupportedLanguage> & supportedLanguages )
    {
        fheroes2::Display & display = fheroes2::Display::instance();

        fheroes2::StandardWindow background( 289, fheroes2::optionsStepY * 3 + 52, true, display );

        const fheroes2::Rect windowRoi = background.activeArea();

        const fheroes2::Rect windowLanguageRoi( languageRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowGraphicsRoi( graphicsRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowAudioRoi( audioRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowHotKeyRoi( hotKeyRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowAnimationRoi( animationRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowPassabilityRoi( passabilityRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowInterfaceTypeRoi( interfaceTypeRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowCursorTypeRoi( cursorTypeRoi + windowRoi.getPosition() );
        const fheroes2::Rect windowScrollSpeedRoi( scrollSpeedRoi + windowRoi.getPosition() );

        const Settings & conf = Settings::Get();
        const auto drawOptions = [&conf, &windowLanguageRoi, &windowGraphicsRoi, &windowAudioRoi, &windowHotKeyRoi, &windowAnimationRoi, &windowPassabilityRoi,
                                  &windowInterfaceTypeRoi, &windowCursorTypeRoi, &windowScrollSpeedRoi]() {
            drawLanguage( windowLanguageRoi, conf.getGameLanguage(), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawGraphics( windowGraphicsRoi, fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawAudioOptions( windowAudioRoi, fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawHotKeyOptions( windowHotKeyRoi, fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawAnimationOptions( windowAnimationRoi );
            drawPassabilityOptions( windowPassabilityRoi );
            drawInterfaceType( windowInterfaceTypeRoi, conf.getInterfaceType(), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawCursorType( windowCursorTypeRoi, conf.isMonochromeCursorEnabled(), fheroes2::UiOptionTextWidth::THREE_ELEMENTS_ROW );
            drawScrollSpeed( windowScrollSpeedRoi, conf.ScrollSpeed() );
        };

        drawOptions();

        const bool isEvilInterface = conf.isEvilInterfaceEnabled();

        fheroes2::Button buttonOk;
        const int buttonOkIcnId = isEvilInterface ? ICN::BUTTON_SMALL_OKAY_EVIL : ICN::BUTTON_SMALL_OKAY_GOOD;
        background.renderButton( buttonOk, buttonOkIcnId, 0, 1, { 0, 11 }, fheroes2::StandardWindow::Padding::BOTTOM_CENTER );

        // Render the whole screen as interface type or resolution could have been changed.
        display.render();
        publishThorEditorSettings( supportedLanguages );

        LocalEvent & le = LocalEvent::Get();
        while ( le.HandleEvents() ) {
            buttonOk.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonOk.area() ) );

            const fheroes2::thor::Action requestedThorAction = fheroes2::thor::takeAction();
            if ( requestedThorAction != fheroes2::thor::Action::NONE ) {
                // Consume one lower-screen request and reject queued duplicates until the panel is rebuilt.
                fheroes2::thor::setEnabledActions( 0 );
            }

            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_CLOSE || le.MouseClickLeft( buttonOk.area() ) || Game::HotKeyCloseWindow() ) {
                break;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_LANGUAGE || le.MouseClickLeft( windowLanguageRoi ) ) {
                return DialogAction::Language;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_GRAPHICS || le.MouseClickLeft( windowGraphicsRoi ) ) {
                return DialogAction::Graphics;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_AUDIO || le.MouseClickLeft( windowAudioRoi ) ) {
                return DialogAction::AudioSettings;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_HOT_KEYS || le.MouseClickLeft( windowHotKeyRoi ) ) {
                return DialogAction::HotKeys;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_ANIMATION || le.MouseClickLeft( windowAnimationRoi ) ) {
                return DialogAction::Animation;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_PASSABILITY || le.MouseClickLeft( windowPassabilityRoi ) ) {
                return DialogAction::Passabiility;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_INTERFACE_TYPE || le.MouseClickLeft( windowInterfaceTypeRoi ) ) {
                return DialogAction::InterfaceType;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_CURSOR_TYPE || le.MouseClickLeft( windowCursorTypeRoi ) ) {
                return DialogAction::CursorType;
            }
            if ( requestedThorAction == fheroes2::thor::Action::EDITOR_SYSTEM_SCROLL_SPEED || le.MouseClickLeft( windowScrollSpeedRoi ) ) {
                return DialogAction::UpdateScrollSpeed;
            }
            if ( le.isMouseWheelUpInArea( windowScrollSpeedRoi ) ) {
                return DialogAction::IncreaseScrollSpeed;
            }
            if ( le.isMouseWheelDownInArea( windowScrollSpeedRoi ) ) {
                return DialogAction::DecreaseScrollSpeed;
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
            else if ( le.isMouseRightButtonPressedInArea( windowAnimationRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Animation" ), _( "Toggle animation of the objects." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowPassabilityRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Passability" ), _( "Toggle display of objects' passability." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowInterfaceTypeRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Interface Type" ), _( "Toggle the type of interface you want to use." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( windowCursorTypeRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Mouse Cursor" ), _( "Toggle colored cursor on or off. This is only an aesthetic choice." ), 0 );
            }
            if ( le.isMouseRightButtonPressedInArea( windowScrollSpeedRoi ) ) {
                fheroes2::showStandardTextMessage( _( "Scroll Speed" ), _( "Sets the speed at which you scroll the window." ), 0 );
            }
            else if ( le.isMouseRightButtonPressedInArea( buttonOk.area() ) ) {
                fheroes2::showStandardTextMessage( _( "Okay" ), _( "Exit this menu." ), 0 );
            }

            // Right-click help temporarily uses Dialog context. Restore the current values and actions afterwards.
            publishThorEditorSettings( supportedLanguages );
        }

        return DialogAction::Close;
    }
}

namespace Editor
{
    void openEditorSettings()
    {
        const fheroes2::thor::UiContextGuard thorContextGuard( fheroes2::thor::UiContext::EDITOR_SYSTEM_OPTIONS );
        const CursorRestorer cursorRestorer( true, Cursor::POINTER );

        // We should write to the configuration file only once to avoid extra I/O operations.
        bool saveConfiguration = false;
        Settings & conf = Settings::Get();
        const std::vector<fheroes2::SupportedLanguage> supportedLanguages = fheroes2::getSupportedLanguages();

        auto redrawEditor = [&conf]() {
            Interface::EditorInterface & editorInterface = Interface::EditorInterface::Get();

            // Since the radar interface has a restorer we must redraw it first to avoid the restorer doing something nasty.
            editorInterface.redraw( Interface::REDRAW_RADAR );

            uint32_t redrawOptions = Interface::REDRAW_ALL;
            if ( conf.isEditorPassabilityEnabled() ) {
                redrawOptions |= Interface::REDRAW_PASSABILITIES;
            }

            editorInterface.redraw( redrawOptions & ( ~Interface::REDRAW_RADAR ) );
        };

        auto rebuildEditor = [&redrawEditor]() {
            Interface::EditorInterface::Get().reset();

            redrawEditor();
        };

        DialogAction action = DialogAction::Configuration;

        while ( action != DialogAction::Close ) {
            switch ( action ) {
            case DialogAction::Configuration:
                action = openEditorOptionsDialog( supportedLanguages );
                break;
            case DialogAction::Language: {
                const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );

                if ( supportedLanguages.size() > 1 ) {
                    selectLanguage( supportedLanguages, fheroes2::getLanguageFromAbbreviation( conf.getGameLanguage() ), true );
                }
                else {
                    assert( supportedLanguages.front() == fheroes2::SupportedLanguage::English );

                    conf.setGameLanguage( fheroes2::getLanguageAbbreviation( fheroes2::SupportedLanguage::English ) );

                    fheroes2::showStandardTextMessage( "Attention", "Your version of Heroes of Might and Magic II does not support any other languages than English.",
                                                       Dialog::OK );
                }

                redrawEditor();
                saveConfiguration = true;
                action = DialogAction::Configuration;
                break;
            }
            case DialogAction::Graphics: {
                const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
                saveConfiguration |= fheroes2::openGraphicsSettingsDialog( rebuildEditor );

                action = DialogAction::Configuration;
                break;
            }
            case DialogAction::AudioSettings: {
                const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
                saveConfiguration |= Dialog::openAudioSettingsDialog( false );

                action = DialogAction::Configuration;
                break;
            }
            case DialogAction::HotKeys: {
                const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
                fheroes2::openHotkeysDialog();

                action = DialogAction::Configuration;
                break;
            }
            case DialogAction::Animation:
                conf.setEditorAnimation( !conf.isEditorAnimationEnabled() );
                saveConfiguration = true;

                if ( conf.isEditorAnimationEnabled() ) {
                    fheroes2::RenderProcessor::instance().startColorCycling();
                }
                else {
                    fheroes2::RenderProcessor::instance().stopColorCycling();
                }

                action = DialogAction::Configuration;
                break;
            case DialogAction::Passabiility:
                conf.setEditorPassability( !conf.isEditorPassabilityEnabled() );
                saveConfiguration = true;

                redrawEditor();

                action = DialogAction::Configuration;
                break;
            case DialogAction::InterfaceType:
                if ( conf.getInterfaceType() == InterfaceType::DYNAMIC ) {
                    conf.setInterfaceType( InterfaceType::GOOD );
                }
                else if ( conf.getInterfaceType() == InterfaceType::GOOD ) {
                    conf.setInterfaceType( InterfaceType::EVIL );
                }
                else {
                    conf.setInterfaceType( InterfaceType::DYNAMIC );
                }
                rebuildEditor();
                saveConfiguration = true;

                action = DialogAction::Configuration;
                break;
            case DialogAction::CursorType:
                conf.setMonochromeCursor( !conf.isMonochromeCursorEnabled() );
                saveConfiguration = true;

                action = DialogAction::Configuration;
                break;
            case DialogAction::UpdateScrollSpeed:
                conf.SetScrollSpeed( ( conf.ScrollSpeed() + 1 ) % ( SCROLL_SPEED_VERY_FAST + 1 ) );
                saveConfiguration = true;

                action = DialogAction::Configuration;
                break;
            case DialogAction::IncreaseScrollSpeed:
                conf.SetScrollSpeed( conf.ScrollSpeed() + 1 );
                saveConfiguration = true;

                action = DialogAction::Configuration;
                break;
            case DialogAction::DecreaseScrollSpeed:
                conf.SetScrollSpeed( conf.ScrollSpeed() - 1 );
                saveConfiguration = true;

                action = DialogAction::Configuration;
                break;
            default:
                break;
            }
        }

        if ( saveConfiguration ) {
            conf.Save( Settings::configFileName );
        }
    }
}
