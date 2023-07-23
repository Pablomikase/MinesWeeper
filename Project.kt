package minesweeper

import kotlin.random.Random

const val ROWS = 9
const val COLLUMNS = 9
var isGameActive = true
var minesInGame = 0
val currentSafeZone: MutableSet<Pair<Int, Int>> = mutableSetOf()

enum class Type {
    FREE, MINE
}


fun main() {

    //board crateion
    val secretBoard = MutableList(ROWS) {
        MutableList(COLLUMNS) { '/' }
    }

    askForStartingMines(secretBoard)
    secretBoard.setHintsAroundMines()

    val publicBoard = secretBoard.generatePublicBoard()
    publicBoard.printBoard()
    secretBoard.printBoard()
    while (isGameActive) {
        println("Set/unset mines marks or claim a cell as free:")
        val userInput = readln().split(" ")
        val selectedXPair = userInput.first().toInt() - 1
        val selectedYPair = userInput[1].toInt() - 1
        val type: Type = if (userInput.last() == "free") Type.FREE else Type.MINE

        if (type == Type.FREE) {
            val selectedValue = secretBoard[selectedXPair][selectedYPair]
            when {
                selectedValue.isDigit() -> {
                    publicBoard[selectedXPair][selectedYPair] = selectedValue
                    publicBoard.printBoard()
                }

                selectedValue == 'X' -> {
                    publicBoard.markAllMines(secretBoard)
                    publicBoard.printBoard()
                    println("You stepped on a mine and failed!")
                    isGameActive = false
                }

                selectedValue == '/' -> {
                    currentSafeZone.clear()
                    showSafePLaceIn_north(selectedXPair + 1, selectedYPair, secretBoard)
                    showSafePLaceIn_south(selectedXPair - 1, selectedYPair, secretBoard)
                    /*showSafePLaceIn_east(selectedXPair, selectedYPair + 1, secretBoard)
                    showSafePLaceIn_west(selectedXPair, selectedYPair - 1, secretBoard)*/
                    currentSafeZone.forEach {
                        publicBoard[it.first][it.second] = secretBoard[it.first][it.second]
                    }
                    publicBoard.printBoard()
                }
            }
        } else {
            if (publicBoard[selectedXPair][selectedYPair] == '.'){
                publicBoard[selectedXPair][selectedYPair] = '*'
            }else if (publicBoard[selectedXPair][selectedYPair] == '*'){
                publicBoard[selectedXPair][selectedYPair] = '.'
            }
            publicBoard.printBoard()
        }

        validateIfGameHasFinished(secretBoard, publicBoard)

    }

}

private tailrec fun showSafePLaceIn_north(
    xPair: Int,
    yPair: Int,
    secretBoard: MutableList<MutableList<Char>>,
) {
    if (validateInputData(xPair, yPair, secretBoard)) return
    val currentValue = secretBoard[xPair][yPair]
    if (currentValue == '/') {
        currentSafeZone.add(Pair(xPair, yPair))
    }
    if (currentValue.isDigit()) {
        currentSafeZone.add(Pair(xPair, yPair))
    }
    if (currentValue == 'X') return

    showSafePLaceIn_east(xPair, yPair + 1, secretBoard)
    showSafePLaceIn_west(xPair, yPair - 1, secretBoard)
    showSafePLaceIn_north(xPair + 1, yPair, secretBoard)
}

private tailrec fun showSafePLaceIn_south(
    xPair: Int,
    yPair: Int,
    secretBoard: MutableList<MutableList<Char>>,
) {
    if (validateInputData(xPair, yPair, secretBoard)) return
    val currentValue = secretBoard[xPair][yPair]
    if (currentValue == '/' || currentValue.isDigit()) {
        currentSafeZone.add(Pair(xPair, yPair))
    }
    if (currentValue.isDigit()) {
        currentSafeZone.add(Pair(xPair, yPair))

    }
    if (currentValue == 'X') return

    showSafePLaceIn_east(xPair, yPair + 1, secretBoard)
    showSafePLaceIn_west(xPair, yPair - 1, secretBoard)
    showSafePLaceIn_south(xPair - 1, yPair, secretBoard)
}

private tailrec fun showSafePLaceIn_east(
    xPair: Int,
    yPair: Int,
    secretBoard: MutableList<MutableList<Char>>,
) {
    if (validateInputData(xPair, yPair, secretBoard)) return
    val currentValue = secretBoard[xPair][yPair]
    if (currentValue == '/') currentSafeZone.add(Pair(xPair, yPair))
    if (currentValue.isDigit()) {
        currentSafeZone.add(Pair(xPair, yPair))

    }
    if (currentValue == 'X') return
    showSafePLaceIn_north(xPair + 1, yPair, secretBoard)
    showSafePLaceIn_south(xPair - 1, yPair, secretBoard)
    showSafePLaceIn_east(xPair, yPair + 1, secretBoard)
}

private tailrec fun showSafePLaceIn_west(
    xPair: Int,
    yPair: Int,
    secretBoard: MutableList<MutableList<Char>>,
) {
    if (validateInputData(xPair, yPair, secretBoard)) return
    val currentValue = secretBoard[xPair][yPair]
    if (currentValue == '/') currentSafeZone.add(Pair(xPair, yPair))
    if (currentValue.isDigit()) {
        currentSafeZone.add(Pair(xPair, yPair))
    }
    if (currentValue == 'X') return
    showSafePLaceIn_north(xPair + 1, yPair, secretBoard)
    showSafePLaceIn_south(xPair - 1, yPair, secretBoard)
    showSafePLaceIn_west(xPair, yPair - 1, secretBoard)
}

/**
 * returns true if dana is invalid
 */
fun validateInputData(xPair: Int, yPair: Int, secretBoard: MutableList<MutableList<Char>>): Boolean {
    if (xPair in 0 until ROWS &&
        yPair in 0 until COLLUMNS
    ) {
        if (currentSafeZone.contains(Pair(xPair, yPair))) return true
        val currentValue = secretBoard[xPair][yPair]
        if (currentValue.isDigit() || currentValue == '/') return false
    } else {
        return true
    }

    return true
}


private fun MutableList<MutableList<Char>>.markAllMines(secretBoard: MutableList<MutableList<Char>>) {
    for (x in 0 until ROWS) {
        for (y in 0 until COLLUMNS) {
            if (secretBoard[x][y] == 'X') {
                this[x][y] = 'X'
            }
        }
    }
}

fun validateIfGameHasFinished(
    secretBoard: MutableList<MutableList<Char>>,
    publicBoard: MutableList<MutableList<Char>>
) {
    //MInes validation
    var totalSelectedMines = 0
    publicBoard.forEach { rowPack ->
        rowPack.forEach {
            if (it == '*') totalSelectedMines++
        }
    }
    var detectedMines = 0
    for (x in 0 until ROWS) {
        for (y in 0 until COLLUMNS) {
            if (publicBoard[x][y] == '*') {
                if (secretBoard[x][y] == 'X') detectedMines++
            }
        }
    }
    if (detectedMines == minesInGame && totalSelectedMines == minesInGame) {
        println("Congratulations! You found all the mines!")
        isGameActive = false
    }
    //Dashes validation
    var detectedDashes = 0
    var detectedNumbers = 0
    var dashes = 0
    var numbers = 0
    secretBoard.forEach { packedRows ->
        packedRows.forEach {
            if (it.isDigit()) numbers++
            if (it == '/') dashes++
        }
    }
    publicBoard.forEach{packedRows ->
        packedRows.forEach {
            if (it.isDigit()) detectedNumbers++
            if (it == '/') detectedDashes++
        }
    }
    if (numbers == detectedNumbers && dashes == detectedDashes){
        println("Congratulations! You found all the mines!")
        isGameActive = false
    }

}

private fun MutableList<MutableList<Char>>.generatePublicBoard(): MutableList<MutableList<Char>> {
    val newBoard = MutableList(this.size) {
        MutableList(this.first().size) { '.' }
    }
    return newBoard
}

fun MutableList<MutableList<Char>>.setHintsAroundMines() {
    for (x in this.indices) {
        for (y in this[x].indices) {
            var minesAround = 0
            try {
                if (this[x - 1][y - 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x][y - 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x + 1][y - 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x - 1][y] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x + 1][y] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x - 1][y + 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x][y + 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            try {
                if (this[x + 1][y + 1] == 'X') ++minesAround
            } catch (_: Exception) {
            }
            if (minesAround > 0 && this[x][y] != 'X') this[x][y] = minesAround.digitToChar()
        }
    }
}

fun askForStartingMines(board: MutableList<MutableList<Char>>) {
    println("How many mines do you want on the field?")
    minesInGame = readln().toInt()
    val randomNumberGenerator = Random
    repeat(minesInGame) {
        board.addNewMine(randomNumberGenerator)
    }
}


fun MutableList<MutableList<Char>>.addNewMine(randomNumberGenerator: Random.Default) {
    val xPoint = randomNumberGenerator.nextInt(ROWS)
    val yPoint = randomNumberGenerator.nextInt(COLLUMNS)
    if (this[xPoint][yPoint] == 'X') {
        this.addNewMine(randomNumberGenerator)
    } else {
        this[xPoint][yPoint] = 'X'
    }
}

fun MutableList<MutableList<Char>>.printBoard() {

    //Print header
    println(
        "\n │123456789│\n" +
                "—│—————————│"
    )
    for (x in 0 until ROWS) {
        print("${x + 1}│")
        for (y in 0 until COLLUMNS) {
            print(this[y][x])
        }
        println("|")
    }
    println("—│—————————│")
}