import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailVerificationPopupComponent } from './email-verification-popup.component';

describe('EmailVerificationPopupComponent', () => {
  let component: EmailVerificationPopupComponent;
  let fixture: ComponentFixture<EmailVerificationPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailVerificationPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailVerificationPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
