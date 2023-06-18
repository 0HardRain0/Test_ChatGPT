# ChatGPT 3.5.ver Tetris
 
import random

# Tetris shapes
SHAPES = [
    [[1, 1, 1, 1]],
    [[1, 1], [1, 1]],
    [[1, 1, 0], [0, 1, 1]],
    [[0, 1, 1], [1, 1, 0]],
    [[1, 1, 1], [0, 1, 0]],
    [[1, 1, 1], [1, 0, 0]],
    [[1, 1, 1], [0, 0, 1]]
]

# Board size
WIDTH = 10
HEIGHT = 20

# Initialize the board
board = [[0] * WIDTH for _ in range(HEIGHT)]


def draw_board():
    # Function to draw the current state of the board
    for row in board:
        line = '|'
        for val in row:
            if val == 0:
                line += ' '
            else:
                line += '#'
        line += '|'
        print(line)
    print('-' * (WIDTH + 2))


def is_collision(shape, x, y):
    # Check if the shape will collide with the board or other shapes
    for row in range(len(shape)):
        for col in range(len(shape[row])):
            if (
                shape[row][col]
                and (board[y + row][x + col] != 0 or x + col < 0 or x + col >= WIDTH or y + row >= HEIGHT)
            ):
                return True
    return False


def rotate_shape(shape):
    # Rotate the shape
    return list(zip(*reversed(shape)))


def place_shape(shape, x, y):
    # Place the shape on the board
    for row in range(len(shape)):
        for col in range(len(shape[row])):
            if shape[row][col]:
                board[y + row][x + col] = shape[row][col]


def clear_lines():
    # Clear complete lines from the board and shift the rest down
    lines_cleared = 0
    row = HEIGHT - 1
    while row >= 0:
        if all(board[row]):
            del board[row]
            board.insert(0, [0] * WIDTH)
            lines_cleared += 1
        else:
            row -= 1
    return lines_cleared


def run_game():
    current_shape = random.choice(SHAPES)
    x, y = WIDTH // 2 - len(current_shape[0]) // 2, 0
    game_over = False

    while not game_over:
        draw_board()

        user_input = input("Enter 'A' to move left, 'D' to move right, 'W' to rotate, or 'Q' to quit: ")
        user_input = user_input.upper()

        if user_input == 'A':
            if not is_collision(current_shape, x - 1, y):
                x -= 1
        elif user_input == 'D':
            if not is_collision(current_shape, x + 1, y):
                x += 1
        elif user_input == 'W':
            rotated_shape = rotate_shape(current_shape)
            if not is_collision(rotated_shape, x, y):
                current_shape = rotated_shape
        elif user_input == 'Q':
            game_over = True

        if not is_collision(current_shape, x, y + 1):
            y += 1
        else:
            place_shape(current_shape, x, y)
            lines_cleared = clear_lines()
            if lines_cleared > 0:
                print("Lines cleared:", lines_cleared)
            current_shape = random.choice(SHAPES)
            x, y = WIDTH // 2 - len(current_shape[0]) // 2, 0
            if is_collision(current_shape, x, y):
                game_over = True

    print("Game Over!")


if __name__ == '__main__':
    run_game()