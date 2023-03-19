package com.inkapplications.ack.android.capture.messages.index

import com.inkapplications.ack.structures.station.Callsign

/**
 * Models the view state for a single conversation item in the index.
 */
data class ConversationItemState(
    /**
     * The displayable name of the correspondent.
     */
    val name: String,

    /**
     * A preview snippet of the latest message in the conversation.
     */
    val messagePreview: String,

    /**
     * The callsign of the correspondent, used as an identifier when navigating.
     */
    val correspondent: Callsign,
)
