/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.group;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.poulpe.model.dto.PermissionForEntity;
import org.jtalks.poulpe.model.dto.PermissionsMap;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.utils.ObjectsFactory;
import org.jtalks.poulpe.web.controller.zkmacro.EntityPermissionsBlock;
import org.jtalks.poulpe.web.controller.zkmacro.PermissionManagementBlock;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class GroupsPermissionsVmTest {

    // SUT
    private GroupsPermissionsVm viewModel;

    @Mock
    private WindowManager windowManager;
    @Mock
    private ComponentService componentService;

    private SelectedEntity<Object> selectedEntity;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        selectedEntity = new SelectedEntity<Object>();

        viewModel = new GroupsPermissionsVm(windowManager, componentService, selectedEntity);
    }

    @Test(dataProvider = "dataProviderForGetBlocks")
    public void testGetBlocks(List<GeneralPermission> permissions, List<Component> components,
            PermissionsMap<GeneralPermission> permissionsMap) {
        when(componentService.getAll()).thenReturn(components);
        when(componentService.getPermissionsMapFor(any(Component.class))).thenReturn(permissionsMap);

        viewModel = new GroupsPermissionsVm(windowManager, componentService, selectedEntity);
        List<EntityPermissionsBlock> blocks = viewModel.getBlocks();

        verify(componentService, atLeastOnce()).getAll();

        for (EntityPermissionsBlock block : blocks) {
            assertNotNull(block.getCaption());
            assertTrue(components.contains(block.getEntity()));

            for (PermissionManagementBlock innerBlock : block.getBlocks()) {
                assertTrue(permissions.contains(innerBlock.getPermission()));
            }
        }
    }

    @Test(dataProvider = "dataProviderForShowGroupsDialog")
    public void testShowGroupsDialog(Entity entity, JtalksPermission permission, String mode) {
        viewModel.showGroupsDialog(entity, permission, mode);

        PermissionForEntity pfe = (PermissionForEntity) selectedEntity.getEntity();

        assertEquals(pfe.getPermission(), permission);
        assertEquals(pfe.getTarget(), entity);
        assertEquals(pfe.isAllowed(), "allow".equalsIgnoreCase(mode));

        verify(windowManager).open(anyString());
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testShowGroupsDialogWithIllegalData() {
        viewModel.showGroupsDialog(null, null, null);
    }

    @DataProvider
    public Object[][] dataProviderForShowGroupsDialog() {
        return new Object[][] { { ObjectsFactory.fakeBranch(), GeneralPermission.READ, "allow" },
                { ObjectsFactory.fakeBranch(), GeneralPermission.ADMIN, "restrict" },
                { ObjectsFactory.fakeSection(), BranchPermission.DELETE_OWN_POSTS, "allow" }, };
    }

    @DataProvider
    public Object[][] dataProviderForGetBlocks() {
        List<GeneralPermission> permissions = GeneralPermission.getAllAsList();
        List<Component> components = ObjectsFactory.createComponents();

        return new Object[][] {
                { Lists.newArrayList(), Lists.newArrayList(), new PermissionsMap<GeneralPermission>() },
                { permissions, components, new PermissionsMap<GeneralPermission>(permissions) },
                { BranchPermission.getAllAsList(), components, new PermissionsMap<GeneralPermission>() }, };
    }
}
