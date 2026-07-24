package com.my.luck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ChessBoard extends View {
    private Paint whitePaint, blackPaint, highlightPaint;
    private int boardSize = 8;
    private float cellSize;
    private int selectedRow = -1, selectedCol = -1;
    private char[][] board = {
        {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
        {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
        {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
    };
    private boolean isWhiteTurn = true;

    public ChessBoard(Context context) {
        super(context);
        whitePaint = new Paint();
        whitePaint.setColor(Color.parseColor("#F0D9B5"));
        blackPaint = new Paint();
        blackPaint.setColor(Color.parseColor("#B58863"));
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.parseColor("#7FC97F"));
        highlightPaint.setAlpha(150);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / boardSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = (getWidth() - cellSize * boardSize) / 2;
        float startY = (getHeight() - cellSize * boardSize) / 2;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                float x = startX + col * cellSize;
                float y = startY + row * cellSize;

                if ((row + col) % 2 == 0) {
                    canvas.drawRect(x, y, x + cellSize, y + cellSize, whitePaint);
                } else {
                    canvas.drawRect(x, y, x + cellSize, y + cellSize, blackPaint);
                }

                // Highlight selected
                if (row == selectedRow && col == selectedCol) {
                    canvas.drawRect(x, y, x + cellSize, y + cellSize, highlightPaint);
                }

                // Draw piece
                char piece = board[row][col];
                if (piece != ' ') {
                    Paint piecePaint = new Paint();
                    piecePaint.setTextSize(cellSize * 0.75f);
                    piecePaint.setColor(Character.isUpperCase(piece) ? Color.WHITE : Color.BLACK);
                    piecePaint.setTextAlign(Paint.Align.CENTER);
                    String symbol = getSymbol(piece);
                    canvas.drawText(symbol, x + cellSize / 2, y + cellSize * 0.8f, piecePaint);
                }
            }
        }
    }

    private String getSymbol(char piece) {
        switch (Character.toLowerCase(piece)) {
            case 'p': return "♟";
            case 'r': return "♜";
            case 'n': return "♞";
            case 'b': return "♝";
            case 'q': return "♛";
            case 'k': return "♚";
            default: return String.valueOf(piece);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float startX = (getWidth() - cellSize * boardSize) / 2;
            float startY = (getHeight() - cellSize * boardSize) / 2;
            int col = (int) ((event.getX() - startX) / cellSize);
            int row = (int) ((event.getY() - startY) / cellSize);

            if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                handleMove(row, col);
            }
        }
        return true;
    }

    private void handleMove(int row, int col) {
        if (selectedRow == -1 && selectedCol == -1) {
            // Select piece
            char piece = board[row][col];
            if (piece != ' ' && isWhiteTurn == Character.isUpperCase(piece)) {
                selectedRow = row;
                selectedCol = col;
                invalidate();
            }
        } else {
            // Move piece
            char piece = board[selectedRow][selectedCol];
            board[row][col] = piece;
            board[selectedRow][selectedCol] = ' ';
            selectedRow = -1;
            selectedCol = -1;
            isWhiteTurn = !isWhiteTurn;
            invalidate();
        }
    }
}