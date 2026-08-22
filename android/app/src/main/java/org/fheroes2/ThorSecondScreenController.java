/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
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
 ***************************************************************************/

package org.fheroes2;

import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;

import org.libsdl.app.SDLActivity;

/**
 * Owns the optional command deck shown on a secondary Android display.
 *
 * Display IDs are deliberately not persisted: Android may assign different IDs after a reboot
 * or after a panel is disabled and enabled again.
 */
final class ThorSecondScreenController implements DisplayManager.DisplayListener
{
    private static final String LOG_TAG = "fheroes2-thor";

    private final GameActivity activity;
    private final DisplayManager displayManager;
    private final Handler mainHandler = new Handler( Looper.getMainLooper() );
    private final Runnable contextPoller = new Runnable() {
        @Override
        public void run()
        {
            if ( presentation instanceof ThorSecondScreenPresentation ) {
                ( (ThorSecondScreenPresentation)presentation ).setGameContext( activity.getThorUiContext() );
            }
            mainHandler.postDelayed( this, 100 );
        }
    };

    private Presentation presentation;
    private boolean isStarted;

    ThorSecondScreenController( final GameActivity activity )
    {
        this.activity = activity;
        displayManager = (DisplayManager)activity.getSystemService( Context.DISPLAY_SERVICE );
    }

    void start()
    {
        if ( displayManager == null ) {
            Log.w( LOG_TAG, "DisplayManager is unavailable; second-screen support is disabled." );
            return;
        }

        displayManager.registerDisplayListener( this, mainHandler );
        isStarted = true;
        updatePresentation();
        mainHandler.removeCallbacks( contextPoller );
        mainHandler.post( contextPoller );
    }

    void stop()
    {
        isStarted = false;
        mainHandler.removeCallbacks( contextPoller );
        if ( displayManager != null ) {
            displayManager.unregisterDisplayListener( this );
        }
        dismissPresentation();
    }

    @Override
    public void onDisplayAdded( final int displayId )
    {
        updatePresentation();
    }

    @Override
    public void onDisplayRemoved( final int displayId )
    {
        updatePresentation();
    }

    @Override
    public void onDisplayChanged( final int displayId )
    {
        updatePresentation();
    }

    private void updatePresentation()
    {
        if ( !isStarted ) {
            return;
        }

        final Display targetDisplay = findSecondaryDisplay();
        if ( targetDisplay == null ) {
            dismissPresentation();
            return;
        }

        if ( presentation != null && presentation.getDisplay().getDisplayId() == targetDisplay.getDisplayId() ) {
            return;
        }

        dismissPresentation();

        final ThorSecondScreenPresentation newPresentation = new ThorSecondScreenPresentation( activity, targetDisplay, this::sendKey );
        newPresentation.setOnDismissListener( dialog -> {
            if ( presentation == dialog ) {
                presentation = null;
            }
        } );

        try {
            newPresentation.show();
            presentation = newPresentation;
            Log.i( LOG_TAG, "Command deck opened on display " + targetDisplay.getDisplayId() + "." );
        }
        catch ( final RuntimeException ex ) {
            Log.e( LOG_TAG, "Unable to open the command deck on display " + targetDisplay.getDisplayId() + ".", ex );
            newPresentation.dismiss();
        }
    }

    private Display findSecondaryDisplay()
    {
        // getDefaultDisplay() is deprecated on recent Android versions, but unlike
        // Context.getDisplay() it is available throughout fheroes2's API 22+ range.
        final Display activityDisplay = activity.getWindowManager().getDefaultDisplay();
        final int activityDisplayId = activityDisplay.getDisplayId();

        // The AYN Thor advertises its other built-in panel as a presentation display. Prefer
        // this category, but accept another active display for compatible dual-display devices.
        for ( final Display display : displayManager.getDisplays( DisplayManager.DISPLAY_CATEGORY_PRESENTATION ) ) {
            if ( display.getDisplayId() != activityDisplayId && display.isValid() ) {
                return display;
            }
        }
        for ( final Display display : displayManager.getDisplays() ) {
            if ( display.getDisplayId() != activityDisplayId && display.isValid() ) {
                return display;
            }
        }

        return null;
    }

    private void dismissPresentation()
    {
        if ( presentation != null ) {
            presentation.dismiss();
            presentation = null;
        }
    }

    private void sendKey( final int keyCode, final boolean pressed )
    {
        final long eventTime = android.os.SystemClock.uptimeMillis();
        final int action = pressed ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
        final KeyEvent event = new KeyEvent( eventTime, eventTime, action, keyCode, 0 );

        // Bypass Android window focus routing: the touch originated in a Presentation window,
        // but the event belongs to the SDL surface on the game display.
        SDLActivity.handleKeyEvent( null, keyCode, event, null );
    }
}
