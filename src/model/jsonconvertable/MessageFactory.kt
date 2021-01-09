package xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable

import xyz.savvamirzoyan.trueithubtalks.model.jsonconvertable.outcome.TextMessageOutcome

object MessageFactory {
    fun textMessage(username: String, message: String): Wrapper<TextMessageOutcome> {
        return Wrapper("new-message", TextMessageOutcome(username, message))
    }
}