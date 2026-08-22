/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

#include "thor_ui.h"

#include <atomic>

#if defined( ANDROID ) && defined( TARGET_AYN_THOR )
#include <jni.h>
#endif

namespace
{
    std::atomic<fheroes2::thor::UiContext> currentContext{ fheroes2::thor::UiContext::FALLBACK };
}

namespace fheroes2::thor
{
    UiContext getUiContext()
    {
        return currentContext.load( std::memory_order_acquire );
    }

    void setUiContext( const UiContext context )
    {
        currentContext.store( context, std::memory_order_release );
    }

    UiContextGuard::UiContextGuard( const UiContext context )
        : _previousContext( getUiContext() )
    {
        setUiContext( context );
    }

    UiContextGuard::~UiContextGuard()
    {
        setUiContext( _previousContext );
    }
}

#if defined( ANDROID ) && defined( TARGET_AYN_THOR )
extern "C" JNIEXPORT jint JNICALL Java_org_fheroes2_GameActivity_nativeGetThorUiContext( JNIEnv *, jclass )
{
    return static_cast<jint>( fheroes2::thor::getUiContext() );
}
#endif
