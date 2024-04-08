package main.service;

import main.api.response.TagsResponse;
import main.dto.TagDTO;
import main.repository.TagsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @InjectMocks
    private TagsServiceImpl tagsService;

    @Mock
    private TagsRepository tagsRepository;

    @Test
    public void testGetTags() {
        TagsResponse expected = new TagsResponse();
        Set<TagDTO> expectedSet = new HashSet<>();
        Set<TagDTO> actualSet = new HashSet<>();
        TagDTO hibernate = factory.createProjection(TagDTO.class);
        hibernate.setWeight(0.5);
        hibernate.setName("hibernate");
        TagDTO java = factory.createProjection(TagDTO.class);
        java.setWeight(1.0);
        java.setName("java");
        actualSet.add(hibernate);
        actualSet.add(java);
        Mockito.when(tagsRepository.getTags()).thenReturn(actualSet);
        expectedSet.add(hibernate);
        expectedSet.add(java);
        expected.setTags(expectedSet);

        TagsResponse actual = tagsService.getTags();

        Assertions.assertEquals(expected, actual);
    }
}