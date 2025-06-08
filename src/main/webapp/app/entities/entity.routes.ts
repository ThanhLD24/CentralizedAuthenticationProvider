import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'centralizedAuthenticationProviderApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'transaction',
    data: { pageTitle: 'centralizedAuthenticationProviderApp.transaction.home.title' },
    loadChildren: () => import('./transaction/transaction.routes'),
  },
  {
    path: 'application-system',
    data: { pageTitle: 'centralizedAuthenticationProviderApp.applicationSystem.home.title' },
    loadChildren: () => import('./application-system/application-system.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
