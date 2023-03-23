package me.ste.stevesseries.base.api.util

data class StevesSeriesVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,

    val minecraftPhase: Int,
    val minecraftMajor: Int,
    val minecraftMinor: Int
) {
    companion object {
        val REGEX = Regex("^(\\d+)\\.(\\d+)\\.(\\d+)-mc(\\d+)\\.(\\d+)(?:\\.(\\d+))?$")

        fun parse(value: String): StevesSeriesVersion {
            val result = REGEX.matchEntire(value) ?: throw IllegalArgumentException("Invalid Steve's Series version: $value")

            return StevesSeriesVersion(
                major = result.groupValues[0].toInt(),
                minor = result.groupValues[1].toInt(),
                patch = result.groupValues[2].toInt(),

                minecraftPhase = result.groupValues[3].toInt(),
                minecraftMajor = result.groupValues[4].toInt(),
                minecraftMinor = result.groupValues[5].toIntOrNull() ?: 0
            )
        }
    }

    override fun toString() = "${this.major}.${this.minor}.${this.patch}-mc${this.minecraftPhase}.${this.minecraftMajor}${if (this.minecraftMinor != 0) ".${this.minecraftMinor}" else ""}"

    fun requireMajor(major: Int) {
        if (this.major != major) {
            throw IllegalStateException("Major version does not match expected version: $major.")
        }
    }
}