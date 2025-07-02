import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private refreshDashboardsub = new Subject<void>();

  refreshDashboard$ = this.refreshDashboardsub.asObservable();

  triggerDAshboardRefresh() {
    this.refreshDashboardsub.next();
  }
}
