//package ru.svanchukov;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.svanchukov.productservice.dto.people.CreateNewPersonDTO;
//import ru.svanchukov.productservice.dto.product.UpdateProductDTO;
//import ru.svanchukov.productservice.entity.People;
//import ru.svanchukov.productservice.repository.PeopleRepository;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class PeopleService {
//
//    @InjectMocks
//    private PeopleService peopleService;
//
//    @Mock
//    private CreateNewPersonDTO createNewPersonDTO;
//
//    @Mock
//    private UpdateProductDTO updateProductDTO;
//
//    @Mock
//    private People people;
//
//    @Mock
//    private PeopleRepository peopleRepository;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void registerPerson() {
//        CreateNewPersonDTO createNewPersonDTO1 = new CreateNewPersonDTO();
//
//        createNewPersonDTO1.setName("Janga");
//        createNewPersonDTO1.setNumber("+79686176574");
//        createNewPersonDTO1.setEmail("janga@yandex.ru");
//        createNewPersonDTO1.setPassword("908790");
//
//        People expectedPeople = new People();
//        expectedPeople.setName(createNewPersonDTO1.getName());
//        expectedPeople.setEmail(createNewPersonDTO1.getEmail());
//        expectedPeople.setNumber(createNewPersonDTO1.getNumber());
//        expectedPeople.setPassword(createNewPersonDTO1.getPassword());
//
//        when(peopleRepository.save(any(People.class))).thenReturn(expectedPeople);
//
////        People registeredPerson = peopleService.registerPerson(createNewPersonDTO1);
////
////        assertNotNull(registeredPerson);
////        assertEquals(expectedPeople.getName(), registeredPerson.getName());
////        assertEquals(expectedPeople.getEmail(), registeredPerson.getEmail());
////        assertEquals(expectedPeople.getNumber(), registeredPerson.getNumber());
////        assertEquals(expectedPeople.getPassword(), registeredPerson.getPassword());
//
//        verify(peopleRepository, times(1)).save(any(People.class));
//
//    }
//
//    @Test
//    public void testFindAll() throws Throwable {
//        People people1 = new People();
//        people1.setName("Ivan");
//        people1.setEmail("ivan@yandex.ru");
//        people1.setNumber("+79629610098");
//        people1.setPassword("9807");
//
//        People people2 = new People();
//        people2.setName("Makil");
//        people2.setEmail("makil@yandex.ru");
//        people2.setNumber("+79629610096");
//        people2.setPassword("9806");
//
//        List<People> mockPeopleList = Arrays.asList(people1, people2);
//
//
//        when(peopleRepository.findAll()).thenReturn(mockPeopleList);
//
//        List<People> peopleList = peopleService.peopleRepository.findAll();
//
//        assertNotNull(peopleList);
//        assertEquals(2, peopleList.size());
//    }
//
//    @Test
//    public void findById() {
//        int id = 2;
//
//        People people1 = new People();
//
//        people1.setId(id);
//        people1.setName("Pasha");
//        people1.setEmail("pasha@gmail.com");
//        people1.setNumber("+79038907856");
//        people1.setPassword("1122");
//
//        when(peopleRepository.findById(id)).thenReturn(Optional.of(people1));
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
