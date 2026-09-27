package com.harshvardhan.matchmate.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RandomUserResponse(
    val results: List<RandomUserDto>,
    val info: RandomUserInfo
)

data class RandomUserInfo(
    val seed: String,
    val results: Int,
    val page: Int,
    val version: String
)

data class RandomUserDto(
    val name: NameDto,
    val location: LocationDto,
    val dob: DobDto,
    val login: LoginDto,
    val picture: PictureDto
)

data class NameDto(
    val title: String,
    val first: String,
    val last: String
)

data class LocationDto(
    val city: String,
    val state: String,
    val country: String
)

data class DobDto(
    val age: Int
)

data class LoginDto(
    val uuid: String
)

data class PictureDto(
    val large: String,
    val medium: String,
    val thumbnail: String
)