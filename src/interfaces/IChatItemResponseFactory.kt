package xyz.savvamirzoyan.trueithubtalks.interfaces

import xyz.savvamirzoyan.trueithubtalks.response.websockets.ChatItemResponse

interface IChatItemResponseFactory {
    fun chatItemResponse(id: Int, title: String, textPreview: String, pictureUrl: String): ChatItemResponse
}