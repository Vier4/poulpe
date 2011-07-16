/**
 * 
 */
package org.jtalks.poulpe.web.controller.component;

import static org.mockito.Mockito.reset;

import java.util.List;

import org.jtalks.poulpe.model.entity.Component;
import org.jtalks.poulpe.service.ComponentService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;


/**
 * @author Dmitriy Sukharev
 *
 */
public class ListPresenterTest {
    
    private ListPresenter presenter = new ListPresenter();

    @Mock
    ComponentService componentService;
    @Mock
    ListView view;
    @Captor
    ArgumentCaptor<List<Component>> componentCaptor;

    @BeforeClass
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter.setComponentService(componentService);
        presenter.initListView(view);
    }
    
    @BeforeMethod
    public void before() {
        reset(componentService);
        reset(view);
    }

}
