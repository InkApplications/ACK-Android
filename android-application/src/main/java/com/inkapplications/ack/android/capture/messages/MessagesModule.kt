package com.inkapplications.ack.android.capture.messages

import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewModelFactory
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewModel
import com.inkapplications.ack.android.capture.messages.conversation.MessageItemViewModelFactory
import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewModel
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MessagesModule {
    @Provides
    fun conversationItemFactory(
        factory: ConversationItemViewModelFactory
    ): ViewModelFactory<MessageData, ConversationItemViewModel> {
        return factory
    }

    @Provides
    fun messageItemFactory(
        factory: MessageItemViewModelFactory
    ): ViewModelFactory<MessageData, MessageItemViewModel> {
        return factory
    }
}
