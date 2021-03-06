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
package org.jtalks.poulpe.web.controller.branch;

import com.google.common.collect.Lists;
import org.apache.commons.lang.math.RandomUtils;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.poulpe.model.dto.GroupsPermissions;
import org.jtalks.poulpe.model.dto.PermissionChanges;
import org.jtalks.poulpe.model.dto.PermissionForEntity;
import org.jtalks.poulpe.model.dto.SecurityGroupList;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.service.BranchService;
import org.jtalks.poulpe.service.GroupService;
import org.jtalks.poulpe.service.PermissionsService;
import org.jtalks.poulpe.model.fixtures.TestFixtures;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.jtalks.poulpe.web.controller.utils.ObjectsFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import static org.testng.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link EditGroupsForBranchPermissionVm}.
 *
 * @author Vyacheslav Zhivaev
 *
 */
public class EditGroupsForBranchPermissionVmTest {
    private EditGroupsForBranchPermissionVm viewModel;

    @Mock BranchService branchService;
    @Mock PermissionsService permissionsService;
    @Mock GroupService groupService;
    @Mock WindowManager windowManager;

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "dataProvider")
    public void cancel(PermissionForEntity permissionForEntity, GroupsPermissions<BranchPermission> groupsPermissions) throws NotFoundException {
        initTest(permissionForEntity, groupsPermissions);
        viewModel.cancel();
        vefiryNothingChanges();
    }

    @Test(dataProvider = "dataProvider")
    public void testAnonimus(PermissionForEntity permissionForEntity, GroupsPermissions<BranchPermission> groupsPermissions) {
        initTest(permissionForEntity, groupsPermissions);
        SecurityGroupList exeptedList = new SecurityGroupList(Lists.<Group>newArrayList(new Group()));
        doReturn(exeptedList).when(groupService).getSecurityGroups();
        List<Group> actualList = viewModel.getFullList();
        assertEquals(actualList,exeptedList.getAllGroups());
    }


    public void initTest(PermissionForEntity permissionForEntity, GroupsPermissions<BranchPermission> groupsPermissions) {
        when(groupService.getSecurityGroups()).thenReturn(new SecurityGroupList());
        when(permissionsService.getPermissionsFor(any(PoulpeBranch.class))).thenReturn(groupsPermissions);

        viewModel = new EditGroupsForBranchPermissionVm(windowManager, permissionsService, groupService,
                ObjectsFactory.createSelectedEntity((Object) permissionForEntity));
    }

    @DataProvider
    public Object[][] dataProvider() {
        // TODO: probably copy-paste
        List<PermissionForEntity> permissionsForEntity = Lists.newArrayList();
        GroupsPermissions<BranchPermission> groupsPermissions = new GroupsPermissions<BranchPermission>(
                BranchPermission.getAllAsList());

        // target entity (PoulpeBranch)
        PoulpeBranch target = TestFixtures.branch();
        // permissions to work with
        BranchPermission[] branchPermissions = BranchPermission.values();

        // building fixtures
        int DATA_PROVIDER_LIMIT = 20;
        int count = Math.min(DATA_PROVIDER_LIMIT, branchPermissions.length);
        for (int i = 0; i < count; i++) {
            permissionsForEntity.add(new PermissionForEntity(target, true, branchPermissions[i]));
            permissionsForEntity.add(new PermissionForEntity(target, false, branchPermissions[i]));

            for (int j = 0, countj = RandomUtils.nextInt(4) + 2; j < countj; j++) {
                groupsPermissions.add(branchPermissions[i], TestFixtures.group(), TestFixtures.group());
            }
        }

        // converting fixtures to usable state, adding same PermissionsMap for each PermissionForEntity
        int permissionsCount = permissionsForEntity.size();
        Object[][] result = new Object[permissionsCount][2];

        for (int i = 0; i < permissionsCount; i++) {
            result[i][0] = permissionsForEntity.get(i);
            result[i][1] = groupsPermissions;
        }

        return result;
    }

    private void vefiryNothingChanges() throws NotFoundException {
        verify(permissionsService, never()).changeGrants(any(PoulpeBranch.class), any(PermissionChanges.class));
        verify(permissionsService, never()).changeRestrictions(any(PoulpeBranch.class), any(PermissionChanges.class));
        verify(branchService, never()).saveBranch(any(PoulpeBranch.class));

        verify(groupService, never()).saveGroup(any(Group.class));
        verify(groupService, never()).deleteGroup(any(Group.class));
    }
}
