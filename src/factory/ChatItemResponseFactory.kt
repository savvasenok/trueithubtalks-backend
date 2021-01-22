package xyz.savvamirzoyan.trueithubtalks.factory

import xyz.savvamirzoyan.trueithubtalks.interfaces.IChatItemResponseFactory
import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

object ChatItemResponseFactory : IChatItemResponseFactory {

    override fun chatItemResponse(id: Int, title: String, textPreview: String, pictureUrl: String): ChatItemResponse {
        return ChatItemResponse(id, title, textPreview, pictureUrl)
    }
}