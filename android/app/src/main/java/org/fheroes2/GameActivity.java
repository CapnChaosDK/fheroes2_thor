/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2022 - 2025                                             *
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

package org.fheroes2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import org.libsdl.app.SDLActivity;

public final class GameActivity extends SDLActivity
{
    private ThorSecondScreenController secondScreenController;

    private static native int nativeGetThorUiContext();
    private static native boolean nativeEnqueueThorAction( int action );
    private static native long nativeGetThorEnabledActionMask();
    private static native String[] nativeGetThorInformationSnapshot( long knownRevision );
    private static native int[] nativeGetThorRadarSnapshot( long knownRevision );
    private static native int[] nativeGetThorVisualSnapshot( long knownRevision );
    private static native boolean nativeIsThorViewportControlEnabled();
    private static native boolean nativeEnqueueThorViewportRequest( float normalizedX, float normalizedY );
    private static native String[] nativeGetThorSelectionSnapshot( long knownRevision );
    private static native boolean nativeEnqueueThorSelectionRequest( int context, long revision, int kind, int id );
    private static native boolean nativeEnqueueThorMarkerInfoRequest( int context, long revision, int kind, int id );
    private static native String[] nativeGetThorTroopSnapshot( long knownRevision );
    private static native int[] nativeGetThorTroopVisualSnapshot( long knownRevision );
    private static native boolean nativeEnqueueThorTroopMoveRequest( int context, long revision, int sourceSide, int sourceSlot,
                                                                     int destinationSide, int destinationSlot, long count );

    int getThorUiContext()
    {
        try {
            return nativeGetThorUiContext();
        }
        catch ( final UnsatisfiedLinkError ex ) {
            // SDL loads the native game library asynchronously during startup.
            return ThorSecondScreenPresentation.CONTEXT_FALLBACK;
        }
    }

    boolean enqueueThorAction( final int action )
    {
        try {
            // A loaded native bridge owns rejection of stale or unavailable actions. Returning
            // true here prevents a rejected semantic action from leaking into the key fallback.
            nativeEnqueueThorAction( action );
            return true;
        }
        catch ( final UnsatisfiedLinkError ex ) {
            // Keep the key-event fallback usable while SDL is loading the native game library.
            return false;
        }
    }

    long getThorEnabledActionMask()
    {
        try {
            return nativeGetThorEnabledActionMask();
        }
        catch ( final UnsatisfiedLinkError ex ) {
            // An older or not-yet-loaded native library can still use the key fallback.
            return -1L;
        }
    }

    String[] getThorInformationSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorInformationSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    int[] getThorRadarSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorRadarSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    int[] getThorVisualSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorVisualSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    boolean isThorViewportControlEnabled()
    {
        try {
            return nativeIsThorViewportControlEnabled();
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return false;
        }
    }

    boolean enqueueThorViewportRequest( final float normalizedX, final float normalizedY )
    {
        try {
            return nativeEnqueueThorViewportRequest( normalizedX, normalizedY );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return false;
        }
    }

    String[] getThorSelectionSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorSelectionSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    boolean enqueueThorSelectionRequest( final int context, final long revision, final int kind, final int id )
    {
        try {
            return nativeEnqueueThorSelectionRequest( context, revision, kind, id );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return false;
        }
    }

    boolean enqueueThorMarkerInfoRequest( final int context, final long revision, final int kind, final int id )
    {
        try {
            return nativeEnqueueThorMarkerInfoRequest( context, revision, kind, id );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return false;
        }
    }

    String[] getThorTroopSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorTroopSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    int[] getThorTroopVisualSnapshot( final long knownRevision )
    {
        try {
            return nativeGetThorTroopVisualSnapshot( knownRevision );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return null;
        }
    }

    boolean enqueueThorTroopMoveRequest( final int context, final long revision, final int sourceSide, final int sourceSlot,
                                         final int destinationSide, final int destinationSlot, final long count )
    {
        try {
            return nativeEnqueueThorTroopMoveRequest( context, revision, sourceSide, sourceSlot, destinationSide, destinationSlot, count );
        }
        catch ( final UnsatisfiedLinkError ex ) {
            return false;
        }
    }

    @Override
    protected void onCreate( final Bundle savedInstanceState )
    {
        final File filesDir = getFilesDir();
        final File externalFilesDir = getExternalFilesDir( null );

        if ( isAssetsDigestChanged( "assets.digest", new File( filesDir, "assets.digest" ) ) ) {
            try {
                extractAssets( "files", externalFilesDir );
                extractAssets( "maps", externalFilesDir );
                // Digest should be updated only after successful extraction of all assets
                extractAssets( "assets.digest", filesDir );
            }
            catch ( final Exception ex ) {
                Log.e( "fheroes2", "Failed to extract assets.", ex );
            }
        }

        super.onCreate( savedInstanceState );

        secondScreenController = new ThorSecondScreenController( this );
        secondScreenController.start();

        // If the minimum set of game assets has not been found, run the toolset activity instead
        if ( !HoMM2AssetManagement.isHoMM2AssetsPresent( externalFilesDir ) ) {
            startActivity( new Intent( this, ToolsetActivity.class ) );

            // Replace this activity with the newly launched activity
            finish();
        }
    }

    @Override
    protected void onDestroy()
    {
        if ( secondScreenController != null ) {
            secondScreenController.stop();
            secondScreenController = null;
        }

        super.onDestroy();

        // When SDL_main() exits, the app process can still remain in memory, and restarting it
        // (for example, using Android Launcher) may result in various errors when SDL attempts
        // to "reinitialize" already initialized things. This workaround terminates the whole
        // process when this activity is destroyed, allowing SDL to initialize normally on the
        // next startup.
        System.exit( 0 );
    }

    @SuppressWarnings( "SameParameterValue" )
    private boolean isAssetsDigestChanged( final String assetsDigestPath, final File localDigestFile )
    {
        try ( final InputStream assetsDigestStream = getAssets().open( assetsDigestPath ) ) {
            try ( final InputStream localDigestStream = Files.newInputStream( localDigestFile.toPath() ) ) {
                if ( Arrays.equals( IOUtils.toByteArray( assetsDigestStream ), IOUtils.toByteArray( localDigestStream ) ) ) {
                    return false;
                }

                Log.i( "fheroes2", "Digest of assets has been changed." );
            }
            catch ( final Exception ex ) {
                Log.i( "fheroes2", "Failed to access the local digest. Considering the digest of assets as changed.", ex );
            }
        }
        catch ( final Exception ex ) {
            Log.e( "fheroes2", "Failed to access the digest of assets. Considering the digest of assets as changed.", ex );
        }

        return true;
    }

    private void extractAssets( final String srcPath, final File dstDir ) throws IOException
    {
        for ( final String path : getAssetsPaths( srcPath ) ) {
            try ( final InputStream in = getAssets().open( path ) ) {
                final File outFile = new File( dstDir, path );

                final File outFileDir = outFile.getParentFile();
                if ( outFileDir != null ) {
                    Files.createDirectories( outFileDir.toPath() );
                }

                try ( final OutputStream out = Files.newOutputStream( outFile.toPath() ) ) {
                    IOUtils.copy( in, out );
                }
            }
        }
    }

    private List<String> getAssetsPaths( final String path ) throws IOException
    {
        final List<String> result = new ArrayList<>();

        final String[] assets = getAssets().list( path );

        // There is no such path at all
        if ( assets == null ) {
            return result;
        }

        // Leaf node
        if ( assets.length == 0 ) {
            result.add( path );

            return result;
        }

        // Regular node
        for ( final String asset : assets ) {
            result.addAll( getAssetsPaths( path + File.separator + asset ) );
        }

        return result;
    }
}
