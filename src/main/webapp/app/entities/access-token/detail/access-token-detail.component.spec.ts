import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AccessTokenDetailComponent } from './access-token-detail.component';

describe('AccessToken Management Detail Component', () => {
  let comp: AccessTokenDetailComponent;
  let fixture: ComponentFixture<AccessTokenDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccessTokenDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./access-token-detail.component').then(m => m.AccessTokenDetailComponent),
              resolve: { accessToken: () => of({ id: 16439 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AccessTokenDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccessTokenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load accessToken on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AccessTokenDetailComponent);

      // THEN
      expect(instance.accessToken()).toEqual(expect.objectContaining({ id: 16439 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
