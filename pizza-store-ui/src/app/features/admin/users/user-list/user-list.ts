import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../../core/services/user/user.service';
import { UserResponse } from '../../../../core/models/user';
import { UserFormComponent } from '../user-form/user-form';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, UserFormComponent],
  templateUrl: './user-list.html'
})
export class UserListComponent implements OnInit {
  users: UserResponse[] = [];
  currentPage = 1;
  totalPages = 1;
  limit = 10;
  
  showForm = false;

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getUsers(this.currentPage, this.limit).subscribe(res => {
      if (res.code === 200) {
        this.users = res.result.content;
        this.totalPages = res.result.totalPages;
        this.currentPage = res.result.currentPage;
        this.cdr.detectChanges();
      }
    });
  }

  toggleStatus(user: UserResponse): void {
    this.userService.updateUserStatus(user.id, !user.active).subscribe(res => {
      if (res.code === 200) {
        user.active = !user.active;
        alert('Cập nhật trạng thái thành công');
      } else {
        alert('Cập nhật thất bại');
      }
    });
  }

  openCreateForm(): void {
    this.showForm = true;
  }

  closeForm(refresh: boolean): void {
    this.showForm = false;
    if (refresh) {
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadUsers();
    }
  }
}
