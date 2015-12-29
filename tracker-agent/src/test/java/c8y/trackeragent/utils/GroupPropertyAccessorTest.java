package c8y.trackeragent.utils;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import c8y.trackeragent.utils.GroupPropertyAccessor;
import c8y.trackeragent.utils.GroupPropertyAccessor.Group;

public class GroupPropertyAccessorTest {

    private File file = new File(aSourcePath());

    @Before
    public void init() throws IOException {
        file.delete();
        file.createNewFile();
    }

    @After
    public void tearDown() throws IOException {
        file.delete();
    }

    @Test
    public void shouldReadAndWriteGroup() throws Exception {
        GroupPropertyAccessor propertyAccessor = newPropertyAccessor();

        Group group1 = aGroup("person_1", "John", "Smith");
        propertyAccessor.write(group1);

        List<Group> groups = propertyAccessor.getGroups();
        assertThat(groups).containsOnly(group1);
        groups = newPropertyAccessor().refresh().getGroups();
        assertThat(groups).containsOnly(group1);

        Group group2 = aGroup("person_2", "Smith", "John");
        propertyAccessor.write(group2);

        groups = propertyAccessor.getGroups();
        assertThat(groups).containsOnly(group1, group2);
        groups = newPropertyAccessor().refresh().getGroups();
        assertThat(groups).containsOnly(group1, group2);
    }

    private Group aGroup(String groupName, String firstName, String lastName) {
        Group group = newPropertyAccessor().createEmptyGroup(groupName);
        group.put("firstName", firstName);
        group.put("lastName", lastName);
        return group;
    }

    private GroupPropertyAccessor newPropertyAccessor() {
        return new GroupPropertyAccessor(aSourcePath(), asList("firstName", "lastName"));
    }

    private String aSourcePath() {
        String root = System.getProperty("user.dir");
        return root + File.separator + "target" + File.separator + "test.properties";
    }

}
