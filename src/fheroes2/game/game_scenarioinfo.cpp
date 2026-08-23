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

#include <algorithm>
#include <cassert>
#include <cstddef>
#include <cstdint>
#include <ostream>
#include <string>
#include <utility>
#include <vector>

#include "audio.h"
#include "audio_manager.h"
#include "cursor.h"
#include "dialog.h"
#include "dialog_selectscenario.h"
#include "difficulty.h"
#include "game.h" // IWYU pragma: associated
#include "game_assets.h"
#include "game_exit.h"
#include "game_hotkeys.h"
#include "game_mainmenu_ui.h"
#include "game_mode.h"
#include "icn.h"
#include "image.h"
#include "localevent.h"
#include "logging.h"
#include "maps_fileinfo.h"
#include "math_base.h"
#include "math_tools.h"
#include "mus.h"
#include "player_info.h"
#include "players.h"
#include "screen.h"
#include "settings.h"
#include "thor_ui.h"
#include "tools.h"
#include "translations.h"
#include "ui_button.h"
#include "ui_dialog.h"
#include "ui_text.h"
#include "ui_tool.h"
#include "ui_window.h"
#include "world.h"

namespace
{
    void outputNewGameInTextSupportMode()
    {
        START_TEXT_SUPPORT_MODE
        COUT( "Select Map for New Game\n" )

        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::MAIN_MENU_SELECT_MAP ) << " to select a map." )
        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::DEFAULT_CANCEL ) << " to close the dialog and return to the Main Menu." )
        COUT( "Press " << Game::getHotKeyNameByEventId( Game::HotKeyEvent::DEFAULT_OKAY ) << " to start the chosen map." )
    }

    void showCurrentlySelectedMapInfoInTextSupportMode( const Maps::FileInfo & mapInfo )
    {
        START_TEXT_SUPPORT_MODE
        COUT( "Currently selected map:\n" )
        COUT( mapInfo.getSummary() )
    }

    void updatePlayers( Players & players, const int humanPlayerCount )
    {
        if ( humanPlayerCount < 2 )
            return;

        int foundHumans = 0;

        for ( size_t i = 0; i < players.size(); ++i ) {
            if ( players[i]->isControlHuman() ) {
                ++foundHumans;
                if ( players[i]->isControlAI() )
                    players[i]->SetControl( CONTROL_HUMAN );
            }

            if ( foundHumans == humanPlayerCount )
                break;
        }
    }

    void DrawScenarioStaticInfo( const fheroes2::Rect & rt )
    {
        fheroes2::Display & display = fheroes2::Display::instance();

        const fheroes2::FontType normalWhiteFont = fheroes2::FontType::normalWhite();

        // text scenario
        fheroes2::Text text( _( "Scenario:" ), normalWhiteFont );
        text.draw( rt.x, rt.y + 9, rt.width, display );

        // text game difficulty
        text.set( _( "Game Difficulty:" ), normalWhiteFont );
        text.draw( rt.x, rt.y + 59, rt.width, display );

        // text opponents
        text.set( _( "Opponents:" ), normalWhiteFont );
        text.draw( rt.x, rt.y + 164, rt.width, display );

        // text class
        text.set( _( "Class:" ), normalWhiteFont );
        text.draw( rt.x, rt.y + 248, rt.width, display );
    }

    void RedrawMapTitle( const Settings & conf, const fheroes2::Rect & maxRoi, const fheroes2::Rect & centeredRoi )
    {
        const auto & info = conf.getCurrentMapInfo();
        fheroes2::Text text{ info.name, fheroes2::FontType::normalWhite(), info.getSupportedLanguage() };

        if ( text.width() > centeredRoi.width ) {
            text.fitToOneRow( maxRoi.width );
            text.draw( maxRoi.x + ( maxRoi.width - text.width() ), maxRoi.y + 3, text.width(), fheroes2::Display::instance() );
        }
        else {
            text.draw( centeredRoi.x, centeredRoi.y + 3, centeredRoi.width, fheroes2::Display::instance() );
        }
    }

    void RedrawDifficultyInfo( const fheroes2::Point & dst )
    {
        const int32_t width = 77;
        const int32_t height = 69;

        for ( int32_t current = Difficulty::EASY; current <= Difficulty::IMPOSSIBLE; ++current ) {
            const int32_t offset = width * current;
            int32_t normalSpecificOffset = 0;
            // Add offset shift because the original difficulty icons have irregular spacing.
            if ( current == Difficulty::NORMAL ) {
                normalSpecificOffset = 1;
            }

            const fheroes2::Text text( Difficulty::String( current ), fheroes2::FontType::smallWhite() );
            text.draw( dst.x + 31 + offset + normalSpecificOffset - ( text.width() / 2 ), dst.y + height, fheroes2::Display::instance() );
        }
    }

    fheroes2::Rect RedrawRatingInfo( const fheroes2::Point & offset, int32_t width_ )
    {
        std::string str( _( "Rating %{rating}%" ) );
        StringReplace( str, "%{rating}", Game::GetRating() );

        const fheroes2::Text text( str, fheroes2::FontType::normalWhite() );
        const int32_t y = offset.y + 372;
        text.draw( offset.x, y, width_, fheroes2::Display::instance() );

        const int32_t textX = ( width_ > text.width() ) ? offset.x + ( width_ - text.width() ) / 2 : 0;

        return { textX, y, text.width(), text.height() };
    }

    fheroes2::GameMode ChooseNewMap( MapsFileInfoList & lists, const int humanPlayerCount )
    {
        assert( !lists.empty() );

        // setup cursor
        const CursorRestorer cursorRestorer( true, Cursor::POINTER );

        fheroes2::Display & display = fheroes2::Display::instance();

        Settings & conf = Settings::Get();
        const bool isEvilInterface = conf.isEvilInterfaceEnabled();

        fheroes2::drawMainMenuScreen();

        fheroes2::StandardWindow background( 388, 397, true, display );

        const fheroes2::Rect roi( background.activeArea() );

        const fheroes2::Point pointDifficultyInfo( roi.x + 8, roi.y + 79 );
        const fheroes2::Point pointOpponentInfo( roi.x + 8, roi.y + 181 );
        const fheroes2::Point pointClassInfo( roi.x + 8, roi.y + 265 );

        const fheroes2::Sprite & scenarioBox = Assets::getImage( isEvilInterface ? ICN::METALLIC_BORDERED_TEXTBOX_EVIL : ICN::METALLIC_BORDERED_TEXTBOX_GOOD, 0 );

        const fheroes2::Rect scenarioBoxRoi( roi.x + ( roi.width - scenarioBox.width() ) / 2, roi.y + 24, scenarioBox.width(), scenarioBox.height() );

        fheroes2::Copy( scenarioBox, 0, 0, display, scenarioBoxRoi );
        fheroes2::addGradientShadow( scenarioBox, display, scenarioBoxRoi.getPosition(), { -5, 5 } );

        const fheroes2::Sprite & difficultyCursor = Assets::getImage( ICN::NGEXTRA, 62 );

        const int32_t difficultyCursorWidth = difficultyCursor.width();
        const int32_t difficultyCursorHeight = difficultyCursor.height();

        // Difficulty selection areas vector.
        std::vector<fheroes2::Rect> coordDifficulty;
        coordDifficulty.reserve( 5 );

        coordDifficulty.emplace_back( roi.x + 8, roi.y + 78, difficultyCursorWidth, difficultyCursorHeight );
        coordDifficulty.emplace_back( roi.x + 85, roi.y + 78, difficultyCursorWidth, difficultyCursorHeight );
        coordDifficulty.emplace_back( roi.x + 161, roi.y + 78, difficultyCursorWidth, difficultyCursorHeight );
        coordDifficulty.emplace_back( roi.x + 238, roi.y + 78, difficultyCursorWidth, difficultyCursorHeight );
        coordDifficulty.emplace_back( roi.x + 315, roi.y + 78, difficultyCursorWidth, difficultyCursorHeight );

        const int32_t buttonSelectWidth = Assets::getImage( ICN::BUTTON_MAP_SELECT_GOOD, 0 ).width();

        fheroes2::Button buttonSelectMaps( scenarioBoxRoi.x + scenarioBoxRoi.width - 6 - buttonSelectWidth, scenarioBoxRoi.y + 5,
                                           isEvilInterface ? ICN::BUTTON_MAP_SELECT_EVIL : ICN::BUTTON_MAP_SELECT_GOOD, 0, 1 );
        buttonSelectMaps.draw();

        fheroes2::Button buttonOk;
        fheroes2::Button buttonCancel;

        const fheroes2::Point buttonOffset( 20, 6 );

        const int buttonOkIcn = isEvilInterface ? ICN::BUTTON_SMALL_OKAY_EVIL : ICN::BUTTON_SMALL_OKAY_GOOD;
        background.renderButton( buttonOk, buttonOkIcn, 0, 1, buttonOffset, fheroes2::StandardWindow::Padding::BOTTOM_LEFT );

        const int buttonCancelIcn = isEvilInterface ? ICN::BUTTON_SMALL_CANCEL_EVIL : ICN::BUTTON_SMALL_CANCEL_GOOD;
        background.renderButton( buttonCancel, buttonCancelIcn, 0, 1, buttonOffset, fheroes2::StandardWindow::Padding::BOTTOM_RIGHT );

        const Maps::FileInfo & mapInfo = [&lists, &conf = std::as_const( conf )]() {
            const Maps::FileInfo & currentMapinfo = conf.getCurrentMapInfo();

            if ( currentMapinfo.filename.empty() ) {
                return lists.front();
            }

            // Make sure that the current map actually exists in the map's list
            const auto iter = std::find_if( lists.begin(), lists.end(), [&currentMapinfo]( const Maps::FileInfo & info ) {
                return info.name == currentMapinfo.name && info.filename == currentMapinfo.filename;
            } );
            if ( iter == lists.end() ) {
                return lists.front();
            }

            return *iter;
        }();

        Players & players = conf.GetPlayers();

        showCurrentlySelectedMapInfoInTextSupportMode( mapInfo );
        conf.setCurrentMapInfo( mapInfo );
        updatePlayers( players, humanPlayerCount );

        // Load players parameters saved from the previous call of the scenario info dialog.
        Game::LoadPlayers( mapInfo.filename, players );

        Interface::PlayersInfo playersInfo;
        playersInfo.UpdateInfo( players, pointOpponentInfo, pointClassInfo );

        const bool isHotSeatGame = humanPlayerCount > 1;
        Player * selectedPlayer = nullptr;
        Player * thorSwapSource = nullptr;
        const auto resetThorPlayerSelection = [&players, &playersInfo, &selectedPlayer, &thorSwapSource]() {
            selectedPlayer = nullptr;
            for ( Player * player : players ) {
                if ( player != nullptr && player->GetControl() == CONTROL_HUMAN ) {
                    selectedPlayer = player;
                    break;
                }
            }
            if ( selectedPlayer == nullptr && !players.empty() ) {
                selectedPlayer = players.front();
            }

            thorSwapSource = nullptr;
            playersInfo.setHighlightedPlayer( selectedPlayer );
        };
        resetThorPlayerSelection();

        DrawScenarioStaticInfo( roi );
        RedrawDifficultyInfo( pointDifficultyInfo );

        const int icnIndex = isEvilInterface ? 1 : 0;

        // Draw difficulty icons.
        for ( int i = 0; i < 5; ++i ) {
            const fheroes2::Sprite & icon = Assets::getImage( ICN::DIFFICULTY_ICON_EASY + i, icnIndex );
            fheroes2::Copy( icon, 0, 0, display, coordDifficulty[i] );
            fheroes2::addGradientShadow( icon, display, { coordDifficulty[i].x, coordDifficulty[i].y }, { -5, 5 } );
        }

        // We calculate the allowed text width according to the select button's width while ensuring symmetric placement of the map title.
        const int32_t boxBorder = 6;
        const int32_t overallBoxTextAreaWidth = ( scenarioBoxRoi.width - ( 2 * boxBorder ) );
        const int32_t maxTextAreaWidth = overallBoxTextAreaWidth - buttonSelectWidth;

        const fheroes2::Rect maxTextRoi{ scenarioBoxRoi.x + boxBorder, scenarioBoxRoi.y + 5, maxTextAreaWidth, 19 };

        const int32_t halfBoxTextAreaWidth = overallBoxTextAreaWidth / 2;
        const int32_t rightSideAvailableTextWidth
            = ( halfBoxTextAreaWidth > buttonSelectWidth ) ? ( halfBoxTextAreaWidth - buttonSelectWidth ) : ( buttonSelectWidth - halfBoxTextAreaWidth );

        const fheroes2::Rect centeredTextRoi{ scenarioBoxRoi.x + boxBorder + buttonSelectWidth, scenarioBoxRoi.y + 5, 2 * rightSideAvailableTextWidth, 19 };

        // Set up restorers.
        fheroes2::ImageRestorer mapTitleArea( display, maxTextRoi.x, maxTextRoi.y, maxTextRoi.width, maxTextRoi.height );
        fheroes2::ImageRestorer opponentsArea( display, roi.x, pointOpponentInfo.y, roi.width, 65 );
        fheroes2::ImageRestorer classArea( display, roi.x, pointClassInfo.y, roi.width, 69 );
        fheroes2::ImageRestorer handicapArea( display, roi.x, pointClassInfo.y + 69, roi.width, 31 );
        fheroes2::ImageRestorer ratingArea( display, buttonOk.area().x + buttonOk.area().width, buttonOk.area().y,
                                            roi.width - buttonOk.area().width - buttonCancel.area().width - 20 * 2, buttonOk.area().height );

        // Map name
        RedrawMapTitle( conf, maxTextRoi, centeredTextRoi );

        playersInfo.RedrawInfo( false );

        fheroes2::Rect ratingRoi = RedrawRatingInfo( roi.getPosition(), roi.width );

        fheroes2::MovableSprite levelCursor( difficultyCursor );
        const int32_t levelCursorOffset = 3;

        switch ( Game::getDifficulty() ) {
        case Difficulty::EASY:
            levelCursor.setPosition( coordDifficulty[0].x - levelCursorOffset, coordDifficulty[0].y - levelCursorOffset );
            break;
        case Difficulty::NORMAL:
            levelCursor.setPosition( coordDifficulty[1].x - levelCursorOffset, coordDifficulty[1].y - levelCursorOffset );
            break;
        case Difficulty::HARD:
            levelCursor.setPosition( coordDifficulty[2].x - levelCursorOffset, coordDifficulty[2].y - levelCursorOffset );
            break;
        case Difficulty::EXPERT:
            levelCursor.setPosition( coordDifficulty[3].x - levelCursorOffset, coordDifficulty[3].y - levelCursorOffset );
            break;
        case Difficulty::IMPOSSIBLE:
            levelCursor.setPosition( coordDifficulty[4].x - levelCursorOffset, coordDifficulty[4].y - levelCursorOffset );
            break;
        default:
            // Did you add a new difficulty mode? Add the corresponding case above!
            assert( 0 );
            break;
        }

        levelCursor.redraw();

        using ThorAction = fheroes2::thor::Action;
        constexpr fheroes2::thor::ActionMask baseScenarioThorActions
            = fheroes2::thor::actionMask( ThorAction::SCENARIO_SELECT_MAP ) | fheroes2::thor::actionMask( ThorAction::SCENARIO_DIFFICULTY_EASY )
              | fheroes2::thor::actionMask( ThorAction::SCENARIO_DIFFICULTY_NORMAL ) | fheroes2::thor::actionMask( ThorAction::SCENARIO_DIFFICULTY_HARD )
              | fheroes2::thor::actionMask( ThorAction::SCENARIO_DIFFICULTY_EXPERT ) | fheroes2::thor::actionMask( ThorAction::SCENARIO_DIFFICULTY_IMPOSSIBLE )
              | fheroes2::thor::actionMask( ThorAction::SCENARIO_START ) | fheroes2::thor::actionMask( ThorAction::MENU_BACK );

        const auto isValidHotSeatSwap = [&conf]( const Player * source, const Player * target ) {
            if ( source == nullptr || target == nullptr ) {
                return false;
            }
            if ( source == target || source->isControlAI() == target->isControlAI() ) {
                return true;
            }

            const PlayerColorsSet flexibleColors = conf.getCurrentMapInfo().AllowCompHumanColors();
            return ( flexibleColors & source->GetColor() ) && ( flexibleColors & target->GetColor() );
        };

        const auto getScenarioThorActions = [&conf, &isValidHotSeatSwap, &players, &selectedPlayer, &thorSwapSource, isHotSeatGame]() {
            fheroes2::thor::ActionMask actions = baseScenarioThorActions;
            if ( players.size() > 1 ) {
                actions |= fheroes2::thor::actionMask( ThorAction::SCENARIO_PREVIOUS_PLAYER )
                           | fheroes2::thor::actionMask( ThorAction::SCENARIO_NEXT_PLAYER );
            }

            if ( selectedPlayer == nullptr ) {
                return actions;
            }

            if ( isHotSeatGame ) {
                if ( players.size() > 1 && ( thorSwapSource == nullptr || isValidHotSeatSwap( thorSwapSource, selectedPlayer ) ) ) {
                    actions |= fheroes2::thor::actionMask( ThorAction::SCENARIO_PLAYER_CONTROL );
                }
            }
            else if ( selectedPlayer->GetControl() != CONTROL_HUMAN && ( conf.getCurrentMapInfo().colorsAvailableForHumans & selectedPlayer->GetColor() ) ) {
                actions |= fheroes2::thor::actionMask( ThorAction::SCENARIO_PLAYER_CONTROL );
            }

            if ( conf.getCurrentMapInfo().AllowChangeRace( selectedPlayer->GetColor() ) ) {
                actions |= fheroes2::thor::actionMask( ThorAction::SCENARIO_PREVIOUS_FACTION )
                           | fheroes2::thor::actionMask( ThorAction::SCENARIO_NEXT_FACTION );
            }
            if ( !selectedPlayer->isControlAI() ) {
                actions |= fheroes2::thor::actionMask( ThorAction::SCENARIO_HANDICAP );
            }

            return actions;
        };

        const auto getHandicapName = []( const Player::HandicapStatus status ) {
            switch ( status ) {
            case Player::HandicapStatus::NONE:
                return "NO HANDICAP";
            case Player::HandicapStatus::MILD:
                return "MILD HANDICAP";
            case Player::HandicapStatus::SEVERE:
                return "SEVERE HANDICAP";
            default:
                assert( 0 );
                return "UNKNOWN HANDICAP";
            }
        };

        const auto publishThorInformation = [&conf, &getHandicapName, &selectedPlayer, &thorSwapSource, humanPlayerCount, isHotSeatGame]() {
            fheroes2::thor::InformationSnapshot snapshot;
            snapshot.context = fheroes2::thor::UiContext::SCENARIO_SETUP;
            snapshot.category = isHotSeatGame ? "HOT SEAT" : "STANDARD";
            snapshot.date = "RATING  " + std::to_string( Game::GetRating() ) + "%";

            if ( selectedPlayer != nullptr ) {
                snapshot.title = selectedPlayer->GetName() + " - " + Color::String( selectedPlayer->GetColor() );
                snapshot.detail = std::string( selectedPlayer->GetControl() == CONTROL_HUMAN ? "HUMAN" : "AI" ) + "     "
                                  + Race::String( selectedPlayer->GetRace() ) + "     " + getHandicapName( selectedPlayer->getHandicapStatus() );
                if ( thorSwapSource != nullptr ) {
                    snapshot.detail += "     SWAP FROM " + std::string( Color::String( thorSwapSource->GetColor() ) );
                }
            }
            else {
                snapshot.title = conf.getCurrentMapInfo().name;
            }

            snapshot.resources = conf.getCurrentMapInfo().name + "     " + Difficulty::String( Game::getDifficulty() ) + "     "
                                 + std::to_string( humanPlayerCount ) + ( humanPlayerCount == 1 ? " PLAYER" : " PLAYERS" );
            fheroes2::thor::publishInformationSnapshot( std::move( snapshot ) );
        };

        const auto redrawThorPlayerInfo = [&display, &getScenarioThorActions, &handicapArea, &opponentsArea, &classArea, &playersInfo, &publishThorInformation,
                                           &roi]() {
            opponentsArea.restore();
            classArea.restore();
            handicapArea.restore();
            playersInfo.RedrawInfo( false );
            display.render( roi );
            fheroes2::thor::setEnabledActions( getScenarioThorActions() );
            publishThorInformation();
        };

        const auto restoreThorScenarioContext = [&getScenarioThorActions, &publishThorInformation]() {
            fheroes2::thor::setUiContext( fheroes2::thor::UiContext::SCENARIO_SETUP );
            fheroes2::thor::setEnabledActions( getScenarioThorActions() );
            publishThorInformation();
        };

        const auto selectDifficulty = [&coordDifficulty, &levelCursor, &ratingArea, &ratingRoi, &roi, &display, &publishThorInformation]( const int32_t index ) {
            assert( index >= Difficulty::EASY && index <= Difficulty::IMPOSSIBLE );
            levelCursor.setPosition( coordDifficulty[index].x - levelCursorOffset, coordDifficulty[index].y - levelCursorOffset );
            levelCursor.redraw();
            Game::saveDifficulty( index );
            ratingArea.restore();
            ratingRoi = RedrawRatingInfo( roi.getPosition(), roi.width );
            display.render( roi );
            publishThorInformation();
        };

        restoreThorScenarioContext();

        fheroes2::validateFadeInAndRender();

        fheroes2::GameMode result = fheroes2::GameMode::QUIT_GAME;

        outputNewGameInTextSupportMode();

        LocalEvent & le = LocalEvent::Get();

        while ( true ) {
            if ( !le.HandleEvents( true, true ) ) {
                if ( Game::processExitEvent() == fheroes2::GameMode::QUIT_GAME ) {
                    fheroes2::fadeOutDisplay();

                    return fheroes2::GameMode::QUIT_GAME;
                }

                continue;
            }

            const ThorAction requestedThorAction = fheroes2::thor::takeAction();

            // press button
            buttonSelectMaps.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonSelectMaps.area() ) );
            buttonOk.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonOk.area() ) );
            buttonCancel.drawOnState( le.isMouseLeftButtonPressedAndHeldInArea( buttonCancel.area() ) );

            // click select
            if ( requestedThorAction == ThorAction::SCENARIO_SELECT_MAP || HotKeyPressEvent( Game::HotKeyEvent::MAIN_MENU_SELECT_MAP )
                 || le.MouseClickLeft( buttonSelectMaps.area() ) ) {
                const Maps::FileInfo * fi = nullptr;
                {
                    const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
                    fi = Dialog::SelectScenario( lists, false );
                }
                if ( lists.empty() ) {
                    // This can happen if all maps have been deleted.
                    result = fheroes2::GameMode::MAIN_MENU;
                    break;
                }

                // The previous dialog might still have a pressed button event. We have to clean the state.
                le.reset();

                if ( fi && fi->filename != conf.getCurrentMapInfo().filename ) {
                    showCurrentlySelectedMapInfoInTextSupportMode( *fi );

                    // The map is changed. Update the map data and do default initialization of players.
                    conf.setCurrentMapInfo( *fi );

                    mapTitleArea.restore();
                    RedrawMapTitle( conf, maxTextRoi, centeredTextRoi );

                    opponentsArea.restore();
                    classArea.restore();
                    handicapArea.restore();
                    ratingArea.restore();

                    updatePlayers( players, humanPlayerCount );
                    playersInfo.UpdateInfo( players, pointOpponentInfo, pointClassInfo );

                    playersInfo.resetSelection();
                    resetThorPlayerSelection();
                    playersInfo.RedrawInfo( false );

                    ratingRoi = RedrawRatingInfo( roi.getPosition(), roi.width );
                    levelCursor.setPosition( coordDifficulty[Game::getDifficulty()].x - levelCursorOffset,
                                             coordDifficulty[Game::getDifficulty()].y - levelCursorOffset ); // From 0 to 4, see: Difficulty enum
                }
                display.render();

                restoreThorScenarioContext();

                outputNewGameInTextSupportMode();
            }
            else if ( requestedThorAction == ThorAction::MENU_BACK || Game::HotKeyPressEvent( Game::HotKeyEvent::DEFAULT_CANCEL )
                      || le.MouseClickLeft( buttonCancel.area() ) ) {
                result = fheroes2::GameMode::MAIN_MENU;
                break;
            }

            if ( requestedThorAction == ThorAction::SCENARIO_START || Game::HotKeyPressEvent( Game::HotKeyEvent::DEFAULT_OKAY )
                 || le.MouseClickLeft( buttonOk.area() ) ) {
                DEBUG_LOG( DBG_GAME, DBG_INFO, "select maps: " << conf.getCurrentMapInfo().filename << ", difficulty: " << Difficulty::String( Game::getDifficulty() ) )
                result = fheroes2::GameMode::START_GAME;

                // Fade-out screen before starting a scenario.
                fheroes2::fadeOutDisplay();
                break;
            }

            bool redrawPlayerInfo = false;
            if ( requestedThorAction == ThorAction::SCENARIO_PREVIOUS_PLAYER || requestedThorAction == ThorAction::SCENARIO_NEXT_PLAYER ) {
                const auto selectedIter = std::find( players.begin(), players.end(), selectedPlayer );
                const size_t selectedIndex = selectedIter == players.end() ? 0 : static_cast<size_t>( selectedIter - players.begin() );
                if ( !players.empty() ) {
                    const size_t nextIndex = requestedThorAction == ThorAction::SCENARIO_PREVIOUS_PLAYER
                                                 ? ( selectedIndex + players.size() - 1 ) % players.size()
                                                 : ( selectedIndex + 1 ) % players.size();
                    selectedPlayer = players[nextIndex];
                    playersInfo.setHighlightedPlayer( selectedPlayer );
                    redrawPlayerInfo = true;
                }
            }
            else if ( requestedThorAction == ThorAction::SCENARIO_PLAYER_CONTROL && selectedPlayer != nullptr ) {
                if ( isHotSeatGame ) {
                    if ( thorSwapSource == nullptr ) {
                        thorSwapSource = selectedPlayer;
                    }
                    else if ( thorSwapSource == selectedPlayer ) {
                        thorSwapSource = nullptr;
                    }
                    else if ( isValidHotSeatSwap( thorSwapSource, selectedPlayer ) && playersInfo.SwapPlayers( *thorSwapSource, *selectedPlayer ) ) {
                        thorSwapSource = nullptr;
                    }
                }
                else if ( selectedPlayer->GetControl() != CONTROL_HUMAN
                          && ( conf.getCurrentMapInfo().colorsAvailableForHumans & selectedPlayer->GetColor() ) ) {
                    const PlayerColorsSet humanColors = players.GetColors( CONTROL_HUMAN, true );
                    if ( Color::Count( humanColors ) == 1 ) {
                        const PlayerColor currentColor = static_cast<PlayerColor>( humanColors );
                        Player * currentPlayer = Players::Get( currentColor );
                        assert( currentPlayer != nullptr );

                        const Player::HandicapStatus handicap = currentPlayer->getHandicapStatus();
                        Players::SetPlayerControl( currentColor, CONTROL_AI | CONTROL_HUMAN );
                        Players::SetPlayerControl( selectedPlayer->GetColor(), CONTROL_HUMAN );
                        selectedPlayer->setHandicapStatus( handicap );
                        currentPlayer->setHandicapStatus( Player::HandicapStatus::NONE );
                    }
                }
                redrawPlayerInfo = true;
            }
            else if ( requestedThorAction == ThorAction::SCENARIO_PREVIOUS_FACTION && selectedPlayer != nullptr
                      && conf.getCurrentMapInfo().AllowChangeRace( selectedPlayer->GetColor() ) ) {
                selectedPlayer->SetRace( Race::getPreviousRace( selectedPlayer->GetRace() ) );
                redrawPlayerInfo = true;
            }
            else if ( requestedThorAction == ThorAction::SCENARIO_NEXT_FACTION && selectedPlayer != nullptr
                      && conf.getCurrentMapInfo().AllowChangeRace( selectedPlayer->GetColor() ) ) {
                selectedPlayer->SetRace( Race::getNextRace( selectedPlayer->GetRace() ) );
                redrawPlayerInfo = true;
            }
            else if ( requestedThorAction == ThorAction::SCENARIO_HANDICAP && selectedPlayer != nullptr && !selectedPlayer->isControlAI() ) {
                switch ( selectedPlayer->getHandicapStatus() ) {
                case Player::HandicapStatus::NONE:
                    selectedPlayer->setHandicapStatus( Player::HandicapStatus::MILD );
                    break;
                case Player::HandicapStatus::MILD:
                    selectedPlayer->setHandicapStatus( Player::HandicapStatus::SEVERE );
                    break;
                case Player::HandicapStatus::SEVERE:
                    selectedPlayer->setHandicapStatus( Player::HandicapStatus::NONE );
                    break;
                default:
                    assert( 0 );
                    break;
                }
                redrawPlayerInfo = true;
            }

            if ( redrawPlayerInfo ) {
                redrawThorPlayerInfo();
            }

            int32_t requestedDifficulty = -1;
            switch ( requestedThorAction ) {
            case ThorAction::SCENARIO_DIFFICULTY_EASY:
                requestedDifficulty = Difficulty::EASY;
                break;
            case ThorAction::SCENARIO_DIFFICULTY_NORMAL:
                requestedDifficulty = Difficulty::NORMAL;
                break;
            case ThorAction::SCENARIO_DIFFICULTY_HARD:
                requestedDifficulty = Difficulty::HARD;
                break;
            case ThorAction::SCENARIO_DIFFICULTY_EXPERT:
                requestedDifficulty = Difficulty::EXPERT;
                break;
            case ThorAction::SCENARIO_DIFFICULTY_IMPOSSIBLE:
                requestedDifficulty = Difficulty::IMPOSSIBLE;
                break;
            default:
                break;
            }

            if ( requestedDifficulty >= 0 ) {
                selectDifficulty( requestedDifficulty );
            }

            if ( le.MouseClickLeft( roi ) ) {
                const int32_t index = GetRectIndex( coordDifficulty, le.getMouseCursorPos() );

                // select difficulty
                if ( 0 <= index ) {
                    selectDifficulty( index );
                }
                // playersInfo
                else {
                    Player * clickedPlayer = playersInfo.GetFromOpponentClick( le.getMouseCursorPos() );
                    if ( clickedPlayer == nullptr ) {
                        clickedPlayer = playersInfo.GetFromOpponentNameClick( le.getMouseCursorPos() );
                    }
                    if ( clickedPlayer == nullptr ) {
                        clickedPlayer = playersInfo.GetFromClassClick( le.getMouseCursorPos() );
                    }
                    if ( clickedPlayer == nullptr ) {
                        clickedPlayer = playersInfo.getPlayerFromHandicapRoi( le.getMouseCursorPos() );
                    }

                    if ( playersInfo.QueueEventProcessing() ) {
                        if ( clickedPlayer != nullptr ) {
                            selectedPlayer = clickedPlayer;
                            thorSwapSource = nullptr;
                            playersInfo.setHighlightedPlayer( selectedPlayer );
                        }
                        redrawThorPlayerInfo();
                    }
                }
            }
            else if ( ( le.isMouseWheelUp() || le.isMouseWheelDown() ) && playersInfo.QueueEventProcessing() ) {
                if ( Player * hoveredPlayer = playersInfo.GetFromClassClick( le.getMouseCursorPos() ) ) {
                    selectedPlayer = hoveredPlayer;
                    thorSwapSource = nullptr;
                    playersInfo.setHighlightedPlayer( selectedPlayer );
                }
                playersInfo.resetSelection();
                redrawThorPlayerInfo();
            }

            if ( le.isMouseRightButtonPressedInArea( roi ) ) {
                if ( le.isMouseRightButtonPressedInArea( buttonSelectMaps.area() ) ) {
                    fheroes2::showStandardTextMessage( _( "Scenario" ), _( "Click here to select which scenario to play." ), Dialog::ZERO );
                }
                else if ( 0 <= GetRectIndex( coordDifficulty, le.getMouseCursorPos() ) ) {
                    fheroes2::showStandardTextMessage(
                        _( "Game Difficulty" ),
                        _( "This lets you change the starting difficulty at which you will play. Higher difficulty levels start you off with fewer resources, and at the higher settings, give extra resources to the computer." ),
                        Dialog::ZERO );
                }
                else if ( le.isMouseRightButtonPressedInArea( ratingRoi ) ) {
                    fheroes2::showStandardTextMessage(
                        _( "Difficulty Rating" ),
                        _( "The difficulty rating reflects a combination of various settings for your game. This number will be applied to your final score." ),
                        Dialog::ZERO );
                }
                else if ( le.isMouseRightButtonPressedInArea( buttonOk.area() ) ) {
                    fheroes2::showStandardTextMessage( _( "Okay" ), _( "Click to accept these settings and start a new game." ), Dialog::ZERO );
                }
                else if ( le.isMouseRightButtonPressedInArea( buttonCancel.area() ) ) {
                    fheroes2::showStandardTextMessage( _( "Cancel" ), _( "Click to return to the main menu." ), Dialog::ZERO );
                }
                else {
                    playersInfo.QueueEventProcessing();
                }
            }
        }

        // Save the changes players parameters before closing this dialog.
        Game::SavePlayers( conf.getCurrentMapInfo().filename, players );

        return result;
    }

    fheroes2::GameMode LoadNewMap()
    {
        Settings & conf = Settings::Get();

        conf.GetPlayers().SetStartGame();

        const Maps::FileInfo & mapInfo = conf.getCurrentMapInfo();

        if ( mapInfo.version == GameVersion::SUCCESSION_WARS || mapInfo.version == GameVersion::PRICE_OF_LOYALTY ) {
            if ( world.LoadMapMP2( mapInfo.filename, ( mapInfo.version == GameVersion::SUCCESSION_WARS ) ) ) {
                return fheroes2::GameMode::START_GAME;
            }

            fheroes2::drawMainMenuScreen();
            fheroes2::showStandardTextMessage( _( "Warning" ), _( "The map is corrupted or doesn't exist." ), Dialog::OK );
            return fheroes2::GameMode::MAIN_MENU;
        }

        assert( mapInfo.version == GameVersion::RESURRECTION );
        if ( world.loadResurrectionMap( mapInfo.filename ) ) {
            return fheroes2::GameMode::START_GAME;
        }

        fheroes2::drawMainMenuScreen();
        fheroes2::showStandardTextMessage( _( "Warning" ), _( "The map is corrupted or doesn't exist." ), Dialog::OK );
        return fheroes2::GameMode::MAIN_MENU;
    }
}

fheroes2::GameMode Game::SelectScenario( const uint8_t humanPlayerCount )
{
    assert( humanPlayerCount >= 1 && humanPlayerCount <= 6 );

    AudioManager::PlayMusicAsync( MUS::MAINMENU, Music::PlaybackMode::RESUME_AND_PLAY_INFINITE );

    MapsFileInfoList maps = Maps::getAllMapFileInfos( humanPlayerCount );
    if ( maps.empty() ) {
        const fheroes2::thor::UiContextGuard dialogContext( fheroes2::thor::UiContext::DIALOG );
        fheroes2::showStandardTextMessage( _( "Warning" ), _( "No maps available!" ), Dialog::OK );
        return fheroes2::GameMode::MAIN_MENU;
    }

    // We must release UI resources for this window before loading a new map. That's why all UI logic is in a separate function.
    const fheroes2::GameMode result = ChooseNewMap( maps, humanPlayerCount );
    if ( result != fheroes2::GameMode::START_GAME ) {
        return result;
    }

    return LoadNewMap();
}

int32_t Game::GetStep4Player( const int32_t currentId, const int32_t width, const int32_t totalCount )
{
    return currentId * width * maxNumOfPlayers / totalCount + ( width * ( maxNumOfPlayers - totalCount ) / ( 2 * totalCount ) );
}
