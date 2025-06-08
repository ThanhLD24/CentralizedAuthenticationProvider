import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import TokenHistoryResolve from './route/token-history-routing-resolve.service';

const tokenHistoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/token-history.component').then(m => m.TokenHistoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/token-history-detail.component').then(m => m.TokenHistoryDetailComponent),
    resolve: {
      tokenHistory: TokenHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/token-history-update.component').then(m => m.TokenHistoryUpdateComponent),
    resolve: {
      tokenHistory: TokenHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/token-history-update.component').then(m => m.TokenHistoryUpdateComponent),
    resolve: {
      tokenHistory: TokenHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tokenHistoryRoute;
