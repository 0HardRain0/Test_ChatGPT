import tkinter as tk
from tkinter import messagebox

class TodoListGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Todo List")

        self.tasks = []

        self.task_entry = tk.Entry(self.root, width=30)
        self.task_entry.pack(pady=10)

        add_button = tk.Button(self.root, text="Add Task", command=self.add_task)
        add_button.pack(pady=5)

        remove_button = tk.Button(self.root, text="Remove Task", command=self.remove_task)
        remove_button.pack(pady=5)

        display_button = tk.Button(self.root, text="Display Tasks", command=self.display_tasks)
        display_button.pack(pady=5)

    def add_task(self):
        task = self.task_entry.get()
        if task:
            self.tasks.append(task)
            messagebox.showinfo("Task Added", f"Task added: {task}")
            self.task_entry.delete(0, tk.END)
        else:
            messagebox.showwarning("Empty Task", "Please enter a task.")

    def remove_task(self):
        task = self.task_entry.get()
        if task:
            if task in self.tasks:
                self.tasks.remove(task)
                messagebox.showinfo("Task Removed", f"Task removed: {task}")
                self.task_entry.delete(0, tk.END)
            else:
                messagebox.showwarning("Task Not Found", f"Task not found: {task}")
        else:
            messagebox.showwarning("Empty Task", "Please enter a task.")

    def display_tasks(self):
        if self.tasks:
            messagebox.showinfo("Tasks", "\n".join(self.tasks))
        else:
            messagebox.showinfo("No Tasks", "No tasks found.")

root = tk.Tk()
todo_list_gui = TodoListGUI(root)
root.mainloop()