/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

#include <cassert>
#include <iostream>

#include "../../src/fheroes2/game/thor_ui.h"

// Host-side bridge regression test. Actual inventory mutation uses ArtifactsBar
// and is covered by the focused Thor hardware checklist.
int main()
{
    using namespace fheroes2::thor;
    setUiContext( UiContext::HERO_MEETING );
    ArtifactSnapshot state;
    state.context = UiContext::HERO_MEETING;
    state.slots.resize( 28 );
    state.slots[0] = { 1, 0, "Locked spellbook", false };
    state.slots[1] = { 2, 0, "Transferable artifact", true };
    state.slots[1].width = 2;
    state.slots[1].height = 1;
    state.slots[1].pixels = { 0xFF010203U, 0x00000000U };
    state.slots[14] = { 1, 0, "Locked spellbook", false };
    state.slots[15] = { 3, 0, "Other artifact", true };
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( 0, state ) );
    assert( state.slots[1].width == 2 && state.slots[1].height == 1 && state.slots[1].pixels.size() == 2 );
    const uint64_t revision = state.revision;

    assert( !enqueueArtifactMoveRequest( revision, -1, 15 ) );
    assert( !enqueueArtifactMoveRequest( revision, 1, 28 ) );
    assert( !enqueueArtifactMoveRequest( revision, 1, 1 ) );
    assert( !enqueueArtifactMoveRequest( revision, 0, 15 ) );
    assert( !enqueueArtifactMoveRequest( revision, 1, 14 ) );
    assert( !enqueueArtifactMoveRequest( revision, 2, 15 ) );
    assert( !enqueueArtifactMoveRequest( revision - 1, 1, 15 ) );

    // Accept one move to an empty slot, reject another pending request, consume once.
    assert( enqueueArtifactMoveRequest( revision, 1, 16 ) );
    assert( !enqueueArtifactMoveRequest( revision, 1, 15 ) );
    publishArtifactSnapshot( state );
    const ArtifactMoveRequest move = takeArtifactMoveRequest();
    assert( move.valid && move.source == 1 && move.destination == 16 );
    assert( !takeArtifactMoveRequest().valid );

    // Same-bag requests support reordering into empty slots.
    assert( enqueueArtifactMoveRequest( revision, 1, 2 ) );
    const ArtifactMoveRequest sameBagMove = takeArtifactMoveRequest();
    assert( sameBagMove.valid && sameBagMove.source == 1 && sameBagMove.destination == 2 );

    // Occupied targets allow swaps even with full bags.
    for ( ArtifactSlotSnapshot & slot : state.slots ) {
        if ( slot.id < 0 ) {
            slot = { 4, 0, "Occupied", true };
        }
    }
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( revision, state ) );
    assert( enqueueArtifactMoveRequest( state.revision, 1, 2 ) );
    const ArtifactMoveRequest sameBagSwap = takeArtifactMoveRequest();
    assert( sameBagSwap.valid && sameBagSwap.source == 1 && sameBagSwap.destination == 2 );
    assert( enqueueArtifactMoveRequest( state.revision, 15, 1 ) );
    assert( takeArtifactMoveRequest().valid );
    assert( !enqueueArtifactMoveRequest( state.revision, 2, 16 ) );

    // Equal scroll IDs with different spells must remain distinct.
    state.slots[2] = { 5, 10, "Scroll", true };
    state.slots[16] = { 5, 11, "Scroll", true };
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( 0, state ) );
    assert( enqueueArtifactMoveRequest( state.revision, 2, 16 ) );
    state.slots[2].spellId = 12;
    publishArtifactSnapshot( state );
    assert( !takeArtifactMoveRequest().valid );
    assert( !enqueueArtifactMoveRequest( state.revision, 2, 16 ) );
    assert( getArtifactSnapshot( 0, state ) );

    // Nested modal transitions invalidate requests even after meeting restoration.
    assert( enqueueArtifactMoveRequest( state.revision, 1, 15 ) );
    {
        const UiContextGuard guard( UiContext::DIALOG );
        assert( !takeArtifactMoveRequest().valid );
        assert( !enqueueArtifactMoveRequest( state.revision, 1, 15 ) );
    }
    assert( !enqueueArtifactMoveRequest( state.revision, 1, 15 ) );
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( 0, state ) );
    assert( enqueueArtifactMoveRequest( state.revision, 1, 15 ) );
    assert( takeArtifactMoveRequest().valid );

    // Malformed or oversized artwork is discarded without invalidating slot metadata.
    state.slots[1].width = 65;
    state.slots[1].height = 1;
    state.slots[1].pixels.assign( 65, 0xFFFFFFFFU );
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( 0, state ) );
    assert( state.slots[1].id == 2 && state.slots[1].width == 0 && state.slots[1].height == 0 && state.slots[1].pixels.empty() );

    state.slots.resize( 27 );
    publishArtifactSnapshot( state );
    assert( getArtifactSnapshot( 0, state ) && state.slots.empty() );
    assert( !enqueueArtifactMoveRequest( state.revision, 1, 15 ) );
    setUiContext( UiContext::ADVENTURE_MAP );
    assert( getArtifactSnapshot( 0, state ) && state.slots.empty() );
    std::cout << "Thor artifact request regression tests passed.\n";
}
