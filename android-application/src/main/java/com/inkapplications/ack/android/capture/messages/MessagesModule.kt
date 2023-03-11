package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewStateFactory
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewState
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewStateFactory
import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewState
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Module
import dagger.Provides

@Module
class MessagesModule {
    @Provides
    fun conversationItemFactory(
        factory: ConversationItemViewStateFactory
    ): ViewStateFactory<MessageData, ConversationItemViewState> {
        return factory
    }

    @Provides
    fun messageItemFactory(
        factory: MessageItemViewStateFactory
    ): ViewStateFactory<MessageData, MessageItemViewState> {
        return factory
    }
}
