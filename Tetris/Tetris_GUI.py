# ChatGPT 3.5 .ver Tetris_GUI.ver

import pygame
import random

# Colors
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
CYAN = (0, 255, 255)
YELLOW = (255, 255, 0)
MAGENTA = (255, 0, 255)
ORANGE = (255, 165, 0)
BLUE = (0, 0, 255)
GREEN = (0, 255, 0)
RED = (255, 0, 0)

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

# Tetris grid size
GRID_SIZE = 30
GRID_WIDTH = 10
GRID_HEIGHT = 20

# Window size
WINDOW_WIDTH = GRID_WIDTH * GRID_SIZE
WINDOW_HEIGHT = GRID_HEIGHT * GRID_SIZE

# Initialize Pygame
pygame.init()

# Create the game window
window = pygame.display.set_mode((WINDOW_WIDTH, WINDOW_HEIGHT))
pygame.display.set_caption("Tetris")

# Clock to control the frame rate
clock = pygame.time.Clock()


def draw_grid():
    # Draw the Tetris grid
    for x in range(0, WINDOW_WIDTH, GRID_SIZE):
        pygame.draw.line(window, WHITE, (x, 0), (x, WINDOW_HEIGHT))
    for y in range(0, WINDOW_HEIGHT, GRID_SIZE):
        pygame.draw.line(window, WHITE, (0, y), (WINDOW_WIDTH, y))


def draw_shape(shape, x, y, color):
    # Draw a Tetris shape on the grid
    for row in range(len(shape)):
        for col in range(len(shape[row])):
            if shape[row][col]:
                pygame.draw.rect(window, color, (x + col * GRID_SIZE, y + row * GRID_SIZE, GRID_SIZE, GRID_SIZE))


def draw_board(board):
    # Draw the current state of the Tetris board
    for row in range(GRID_HEIGHT):
        for col in range(GRID_WIDTH):
            if board[row][col]:
                pygame.draw.rect(window, board[row][col], (col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE))
            else:
                pygame.draw.rect(window, BLACK, (col * GRID_SIZE, row * GRID_SIZE, GRID_SIZE, GRID_SIZE), 1)


def rotate_shape(shape):
    # Rotate the shape
    return list(zip(*reversed(shape)))


def is_collision(shape, x, y, board):
    # Check if the shape will collide with the board or other shapes
    for row in range(len(shape)):
        for col in range(len(shape[row])):
            if (
                shape[row][col]
                and (board[y + row][x + col] or x + col < 0 or x + col >= GRID_WIDTH or y + row >= GRID_HEIGHT)
            ):
                return True
    return False


def clear_lines(board):
    # Clear complete lines from the board and shift the rest down
    lines_cleared = 0
    row = GRID_HEIGHT - 1
    while row >= 0:
        if all(board[row]):
            del board[row]
            board.insert(0, [0] * GRID_WIDTH)
            lines_cleared += 1
        else:
            row -= 1
    return lines_cleared


def run_game():
    # Initialize the game
    board = [[0] * GRID_WIDTH for _ in range(GRID_HEIGHT)]
    current_shape = random.choice(SHAPES)
    x, y = GRID_WIDTH // 2 - len(current_shape[0]) // 2, 0
    game_over = False

    while not game_over:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                return

            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    if not is_collision(current_shape, x - 1, y, board):
                        x -= 1
                elif event.key == pygame.K_RIGHT:
                    if not is_collision(current_shape, x + 1, y, board):
                        x += 1
                elif event.key == pygame.K_DOWN:
                    if not is_collision(current_shape, x, y + 1, board):
                        y += 1
                elif event.key == pygame.K_UP:
                    rotated_shape = rotate_shape(current_shape)
                    if not is_collision(rotated_shape, x, y, board):
                        current_shape = rotated_shape
                elif event.key == pygame.K_ESCAPE:
                    pygame.quit()
                    return

        if not is_collision(current_shape, x, y + 1, board):
            y += 1
        else:
            place_shape(current_shape, x, y, board)
            lines_cleared = clear_lines(board)
            if lines_cleared > 0:
                print("Lines cleared:", lines_cleared)
            current_shape = random.choice(SHAPES)
            x, y = GRID_WIDTH // 2 - len(current_shape[0]) // 2, 0
            if is_collision(current_shape, x, y, board):
                game_over = True

        # Clear the window
        window.fill(BLACK)

        # Draw the Tetris grid
        draw_grid()

        # Draw the current shape
        draw_shape(current_shape, x * GRID_SIZE, y * GRID_SIZE, CYAN)

        # Draw the board
        draw_board(board)

        # Update the display
        pygame.display.update()

        # Limit the frame rate
        clock.tick(10)


def place_shape(shape, x, y, board):
    # Place the shape on the board
    for row in range(len(shape)):
        for col in range(len(shape[row])):
            if shape[row][col]:
                board[y + row][x + col] = shape[row][col]


if __name__ == '__main__':
    run_game()