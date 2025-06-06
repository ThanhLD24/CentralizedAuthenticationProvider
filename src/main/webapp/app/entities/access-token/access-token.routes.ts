import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import AccessTokenResolve from './route/access-token-routing-resolve.service';

const accessTokenRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/access-token.component').then(m => m.AccessTokenComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/access-token-detail.component').then(m => m.AccessTokenDetailComponent),
    resolve: {
      accessToken: AccessTokenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/access-token-update.component').then(m => m.AccessTokenUpdateComponent),
    resolve: {
      accessToken: AccessTokenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/access-token-update.component').then(m => m.AccessTokenUpdateComponent),
    resolve: {
      accessToken: AccessTokenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default accessTokenRoute;
