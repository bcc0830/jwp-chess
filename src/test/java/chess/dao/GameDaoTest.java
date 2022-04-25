package chess.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import chess.domain.GameState;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class GameDaoTest {

    private static final long testGameId = 2;

    @Autowired
    private GameDao gameDao;

    @DisplayName("게임 저장 테스트")
    @Test
    void save() {
        gameDao.save(testGameId);
    }

    @DisplayName("게임 조회 테스트")
    @Test
    void load() {
        gameDao.save(testGameId);

        Optional<GameState> maybeGameState = gameDao.load(testGameId);
        GameState actual = maybeGameState.orElseGet(() -> fail("데이터가 없습니다."));

        assertThat(actual).isEqualTo(GameState.READY);
    }

    @DisplayName("게임 정보 업데이트 테스트")
    @Test
    void update() {
        gameDao.save(testGameId);
        gameDao.updateState(testGameId, GameState.WHITE_RUNNING);

        Optional<GameState> maybeGameState = gameDao.load(testGameId);
        GameState actual = maybeGameState.orElseGet(() -> fail("데이터가 없습니다."));

        assertThat(actual).isEqualTo(GameState.WHITE_RUNNING);
    }

    @DisplayName("게임 삭제 테스트")
    @Test
    void delete() {
        gameDao.delete(testGameId);
    }
}
