# Hangman Game

A multiplayer Hangman game implemented in Java with client-server architecture.

## Features

- Multiplayer support with rooms
- Turn-based gameplay
- Point system for correct guesses
- Word list management
- Player management (away status, spectator mode)
- Score tracking

## Requirements

- Java 8 or higher
- Maven (for building)

## Building and Running

### Server
```bash
javac Server.java
java Server <port>
```

### Client
```bash
javac Client.java
java Client <host> <port>
```

## Commands

### Client Commands
- `/name <name>` - Set your player name
- `/connect` - Connect to the current room
- `/create <room>` - Create a new room
- `/join <room>` - Join an existing room
- `/guess <word>` - Guess the entire word
- `/letter <letter>` - Guess a single letter
- `/quit` - Disconnect from the server

## Game Rules

1. Players take turns guessing letters or the entire word
2. Correct letter guesses earn 10 points per occurrence
3. Correct word guesses earn 20 points per missing letter
4. Incorrect guesses result in a strike
5. Game ends when:
   - Word is guessed correctly
   - Maximum strikes (6) are reached
   - All players have had their turn

## Project Structure

- `Server.java` - Main server class
- `ServerThread.java` - Handles individual client connections
- `Room.java` - Base room class
- `GameRoom.java` - Game-specific room functionality
- `Client.java` - Client application
- `Payload.java` - Base payload class
- `PointsPayload.java` - Points-related payload
- `Player.java` - Player data management
- `words.txt` - Word list for the game

## License

This project is licensed under the MIT License. 