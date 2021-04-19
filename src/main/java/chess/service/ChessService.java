package chess.service;

import chess.domain.board.Board;
import chess.domain.game.ChessGame;
import chess.domain.piece.PieceFactory;
import chess.domain.piece.Position;
import chess.repository.GameRepository;
import chess.web.dto.GameDto;
import chess.web.dto.MessageDto;
import chess.web.dto.StatusDto;
import org.springframework.stereotype.Service;

@Service
public class ChessService {

    private final GameRepository gameRepository;

    public ChessService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public MessageDto end(String gameId) {
        ChessGame chessGame = gameRepository.findByGameIdFromCache(gameId);

        chessGame.end();

        return new MessageDto("finished");
    }

    public GameDto loadByGameId(String gameId) {
        ChessGame chessGame = gameRepository.findByGameIdFromDB(gameId);
        gameRepository.saveToCache(gameId, chessGame);

        return new GameDto(chessGame);
    }

    public GameDto startNewGame(String gameId) {
        ChessGame chessGame = saveGameAndStart(gameId);

        return new GameDto(chessGame);
    }

    public StatusDto getStatus(String gameId) {
        ChessGame chessGame = gameRepository.findByGameIdFromCache(gameId);

        double whiteScore = chessGame.getWhiteScore();
        double blackScore = chessGame.getBlackScore();

        return new StatusDto(whiteScore, blackScore);
    }

    private ChessGame saveGameAndStart(String gameId) {
        ChessGame chessGame = new ChessGame(new Board(PieceFactory.createPieces()));
        chessGame.start();

        if (gameRepository.isGameIdExistingInDB(gameId)) {
            throw new IllegalArgumentException("이미 존재하는 게임 아이디 입니다.");
        }

        gameRepository.saveToCache(gameId, chessGame);

        return chessGame;
    }

    public MessageDto save(String gameId) {
        ChessGame chessGame = gameRepository.findByGameIdFromCache(gameId);
        saveGameToDB(gameId, chessGame);

        return new MessageDto("저장 완료");
    }

    private void saveGameToDB(String gameId, ChessGame chessGame) {
        if (gameRepository.isGameIdExistingInDB(gameId)) {
            gameRepository.updateToDB(gameId, chessGame);
            return;
        }

        gameRepository.saveToDB(gameId, chessGame);
    }

    public GameDto move(String gameId, String source, String target) {
        ChessGame chessGame = gameRepository.findByGameIdFromCache(gameId);

        return executeMove(
                Position.ofChessPiece(source),
                Position.ofChessPiece(target),
                chessGame
        );
    }

    private GameDto executeMove(Position sourcePosition, Position targetPosition, ChessGame chessGame) {
        chessGame.move(sourcePosition, targetPosition);

        return new GameDto(chessGame);
    }



}
