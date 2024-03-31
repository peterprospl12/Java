import org.example.Mage;
import org.example.MageController;
import org.example.MageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MageControllerTest {

    @Mock
    MageRepository repository;

    @InjectMocks
    MageController mageController;

    @Test
    public void deleting_existing_Mage() {
        String existingMageName = "existingMage";
        doNothing().when(repository).delete(existingMageName);
        String result = mageController.delete(existingMageName);
        assertThat(result).isEqualTo("done");
    }

    @Test
    public void deleting_non_existing_Mage() {
        String nonExistingMageName = "nonExistingMage";
        doThrow(IllegalArgumentException.class).when(repository).delete(nonExistingMageName);
        String result = mageController.delete(nonExistingMageName);
        assertThat(result).isEqualTo("not found");
    }

    @Test
    public void find_existing_Mage() {
        String existingMageName = "existingMage";
        Mage existingMage = new Mage(existingMageName, 10);
        when(repository.find(existingMageName)).thenReturn(Optional.of(existingMage));
        String result = mageController.find(existingMageName);
        assertThat(result).isEqualTo(existingMage.toString());
    }

    @Test
    public void find_non_existing_Mage() {
        String nonExistingMageName = "nonExistingMage";
        when(repository.find(nonExistingMageName)).thenReturn(Optional.empty());
        String result = mageController.find(nonExistingMageName);
        assertThat(result).isEqualTo("not found");
    }

    @Test
    public void save_non_existing_Mage() {
        String nonExistingMageName = "nonExistingMage";
        int nonExistingMageLevel = 10;

        doNothing().when(repository).save(new Mage(nonExistingMageName, nonExistingMageLevel));
        String result = mageController.save(nonExistingMageName, nonExistingMageLevel);
        assertThat(result).isEqualTo("done");

    }

    @Test
    public void save_existing_Mage() {
        String existingMageName = "existingMage";
        int existingMageLevel = 10;

        doThrow(IllegalArgumentException.class).when(repository).save(new Mage(existingMageName, existingMageLevel));
        String result = mageController.save(existingMageName, existingMageLevel);
        assertThat(result).isEqualTo("bad request");

    }
}