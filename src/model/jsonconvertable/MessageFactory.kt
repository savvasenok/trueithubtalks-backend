package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable

import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome.TextMessageOutcome

object MessageFactory {
    fun textMessage(username: String, sender: String, message: String): Wrapper<TextMessageOutcome> {
        return Wrapper("new-message", TextMessageOutcome(username, sender, message))
    }

    fun messageHistory(messages: ArrayList<TextMessageOutcome>): Wrapper<ArrayList<TextMessageOutcome>> {
        return Wrapper("message-history", messages)
    }

    fun chatInFeedUpdate(chat: Chat): Wrapper<Chat> {
        return Wrapper("chat-feed-update", chat)
    }

    fun chatsFeedDownload(chats: ArrayList<Chat>): Wrapper<ArrayList<Chat>> {
        return Wrapper("chat-feed-download", chats)
    }
}