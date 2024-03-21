package com.jaydev.webview.sample.domain.usecase

import com.jaydev.webview.sample.domain.model.Token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTokenUseCase {
    operator fun invoke(): Flow<Result<Token>> {
        return flow {
            emit(Result.success(Token("accessToken", "refreshToken")))
        }
    }
}

class RefreshTokenUseCase {
    operator fun invoke(): Flow<Result<Token>> {
        return flow {
            emit(Result.success(Token("updatedAccessToken", "updatedRefreshToken")))
        }
    }
}