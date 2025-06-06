import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ApplicationSystemResolve from './route/application-system-routing-resolve.service';

const applicationSystemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/application-system.component').then(m => m.ApplicationSystemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/application-system-detail.component').then(m => m.ApplicationSystemDetailComponent),
    resolve: {
      applicationSystem: ApplicationSystemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/application-system-update.component').then(m => m.ApplicationSystemUpdateComponent),
    resolve: {
      applicationSystem: ApplicationSystemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/application-system-update.component').then(m => m.ApplicationSystemUpdateComponent),
    resolve: {
      applicationSystem: ApplicationSystemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default applicationSystemRoute;
