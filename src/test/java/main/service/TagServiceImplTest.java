package main.service;

import main.api.response.TagsResponse;
import main.dto.TagDTO;
import main.repository.TagsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SpringBootTest
public class TagServiceImplTest {

    @Autowired
    private TagsServiceImpl tagsService;

    @MockBean
    private TagsRepository tagsRepository;

    @BeforeEach
    public void setUp() {
        TagDTOImpl hibernate = new TagDTOImpl("hibernate", 0.5);
        TagDTOImpl java = new TagDTOImpl("java", 1.0);
        Set<TagDTO> actualSet = new HashSet<>();
        actualSet.add(hibernate);
        actualSet.add(java);
        Mockito.when(tagsRepository.getTags()).thenReturn(actualSet);
    }

    @Test
    public void testGetTags() {
        TagsResponse expected = new TagsResponse();
        Set<TagDTO> expectedSet = new HashSet<>();
        TagDTOImpl hibernate = new TagDTOImpl("hibernate", 0.5);
        TagDTOImpl java = new TagDTOImpl("java", 1.0);
        expectedSet.add(hibernate);
        expectedSet.add(java);
        expected.setTags(expectedSet);
        TagsResponse actual = tagsService.getTags();

        Assertions.assertEquals(expected, actual);
    }
}

class TagDTOImpl implements TagDTO {

    private final String name;
    private final double weight;

    public TagDTOImpl(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Double getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDTOImpl that = (TagDTOImpl) o;
        return weight == that.weight && name.equals(that.name);
    }
}
