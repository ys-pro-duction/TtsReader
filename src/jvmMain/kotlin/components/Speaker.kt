package components

enum class Speaker(name: String, val id: Int) {
    Raquel("Raquel", 0),
    Monica("Mónica", 1),
    Tokyo("Tokyo", 2),
    Alicia("Alicia", 3),
    Julia("Julia", 4),
    Berlin("Berlin", 5),
    Sergio("Sergio", 6),
    Alison("Alison", 7),
    Tatiana("Tatiana", 8),
    Denver("Denver", 9),
    Moscow("Moscow", 10);

    companion object {
        fun getById(id: Int): Speaker {
            return all.firstOrNull { it.id == id } ?: Raquel
        }

        val all = listOf(
            Raquel,
            Monica,
            Tokyo,
            Alicia,
            Julia,
            Berlin,
            Sergio,
            Alison,
            Tatiana,
            Denver,
            Moscow
        )
    }

}