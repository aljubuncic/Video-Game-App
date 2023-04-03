package ba.unsa.etf.rma.videogameapp

data class UserReview(
    override val username: String,
    override val timestamp: Long,
    val review: String
):UserImpression()

