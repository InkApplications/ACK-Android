package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewStateFactory
import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewState
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MessagesModule {
    @Provides
    fun conversationItemFactory(
        factory: ConversationItemViewStateFactory
    ): ViewStateFactory<MessageData, ConversationItemViewState> {
        return factory
    }
}
