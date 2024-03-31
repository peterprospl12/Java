import org.example.Mage;
import org.example.MageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MageRepositoryTest {
    @Mock
    Mage mockMage;

    @InjectMocks
    MageRepository mageRepository;

    @BeforeEach
    public void setUp() {
        when(mockMage.getName()).thenReturn("testMage");
    }

    @Test
    public void givenNewMage_whenSave_thenCorrectlySaved() {
        assertThat(mageRepository.getCollection()).isEmpty();
        mageRepository.save(mockMage);
        Collection<Mage> temp = mageRepository.getCollection();
        assertThat(temp).containsExactly(mockMage);
        assertThat(temp.size()).isEqualTo(1);
    }

    @Test
    public void givenSavedMage_whenFind_thenCorrectlyFound() {
        mageRepository.save(mockMage);
        Optional<Mage> foundMage = mageRepository.find(mockMage.getName());
        assertThat(foundMage).isPresent().get().isEqualTo(mockMage);

    }

    @Test
    public void givenSavedMage_whenDelete_thenCorrectlyDeleted() {
        mageRepository.save(mockMage);
        assertThat(mageRepository.getCollection()).containsExactly(mockMage);
        mageRepository.delete(mockMage.getName());
        assertThat(mageRepository.getCollection()).isEmpty();
    }

    @Test
    public void givenDuplicateMage_whenSave_thenThrowException() {
        mageRepository.save(mockMage);
        assertThrows(IllegalArgumentException.class, () -> mageRepository.save(mockMage));
        assertThat(mageRepository.getCollection()).containsOnly(mockMage);
        assertThat(mageRepository.getCollection().size()).isEqualTo(1);
    }

    @Test
    public void givenNonexistentMage_whenDelete_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> mageRepository.delete(mockMage.getName()));
        assertThat(mageRepository.getCollection()).isEmpty();
    }

    @Test
    public void givenNonexistentMage_whenFind_thenReturnEmptyOptional() {
        Optional<Mage> result = mageRepository.find("nonexistentMageName");
        assertThat(result).isEmpty();
    }
}

