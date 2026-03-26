package filmotokio.service;

import filmotokio.domain.Person;
import filmotokio.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonRepository personRepository;

    public boolean savePerson(Person person) {
        logger.info("[PersonService] - Attempting to create new person - Name: \"{}\", Surname: \"{}\", Type: {}", 
                   person.getName(), person.getSurname(), person.getType());

        if (personRepository.findByNameAndSurname(person.getName(), person.getSurname()).isPresent()) {
            logger.warn("[PersonService] - WARNING: Person already exists - Name: \"{}\", Surname: \"{}\"", 
                       person.getName(), person.getSurname());
            return false;
        }

        try {
            Person savedPerson = personRepository.save(person);
            logger.info("[PersonService] - Person created successfully - ID: {}, Name: \"{} {}\", Type: {}", 
                       savedPerson.getId(), savedPerson.getName(), savedPerson.getSurname(), savedPerson.getType());
            return true;
        } catch (Exception e) {
            logger.error("[PersonService] - ERROR creating person - Name: \"{}\", Surname: \"{}\", Exception: {}", 
                        person.getName(), person.getSurname(), e.getMessage(), e);
            return false;
        }
    }

}
