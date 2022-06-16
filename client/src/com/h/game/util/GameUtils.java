package com.h.game.util;

public class GameUtils {
    public static boolean gameOver(int map[][], int target) {
        for (int i = 0; i < map.length - 4; i++) {
            for (int j = 0; j < map.length - 4; j++) {
                if (map[i][j] == target && map[i + 1][j + 1] == target && map[i + 2][j + 2] == target && map[i + 3][j + 3] == target && map[i + 4][j + 4] == target) {
                    return true;
                }
            }
        }

        for (int i = 4; i < map.length; i++) {
            for (int j = 4; j < map.length; j++) {
                if (map[i][j] == target && map[i - 1][j - 1] == target && map[i - 2][j - 2] == target && map[i - 3][j - 3] == target && map[i - 4][j - 4] == target) {
                    return true;
                }
            }
        }
        for (int i = 4; i < map.length; i++) {
            for (int j = 1; j < map.length - 4; j++) {
                if (map[i][j] == target && map[i - 1][j + 1] == target && map[i - 2][j + 2] == target && map[i - 3][j + 3] == target && map[i - 4][j + 4] == target) {
                    return true;
                }
            }
        }
        for (int i = 1; i < map.length - 4; i++) {
            for (int j = 4; j < map.length; j++) {
                if (map[i][j] == target && map[i + 1][j - 1] == target && map[i + 2][j - 2] == target && map[i + 3][j - 3] == target && map[i + 4][j - 4] == target) {
                    return true;
                }
            }
        }
        for (int i = 0; i < map.length; i++) {
            for (int j = 4; j < map.length - 4; j++) {
                if (map[i][j] == target && map[i][j - 1] == target && map[i][j - 2] == target && map[i][j - 3] == target && map[i][j - 4] == target) {
                    return true;
                }
                if (map[i][j] == target && map[i][j + 1] == target && map[i][j + 2] == target && map[i][j + 3] == target && map[i][j + 4] == target) {
                    return true;
                }
            }
        }
        for (int i = 4; i < map.length - 4; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j] == target && map[i + 1][j] == target && map[i + 2][j] == target && map[i + 3][j] == target && map[i + 4][j] == target) {
                    return true;
                }
                if (map[i][j] == target && map[i - 1][j] == target && map[i - 2][j] == target && map[i - 3][j] == target && map[i - 4][j] == target) {
                    return true;
                }
            }
        }
        return false;
    }

}
