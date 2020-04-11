<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

//user
Route::get('users','UserController@getAllUsers')->name('getAllUsers');
Route::get('users/{userID}', 'UserController@getUserByUserID')->name('getUserByUserID');
Route::post('users','UserController@addNewUser')->name('addNewUser');
Route::put('users','UserController@updateUser')->name('updateUser');
Route::delete('users/{userID}','UserController@deleteUser')->name('deleteUser');

//board
Route::get('boards', 'BoardController@getAllBoards')->name('getAllBoards');
Route::get('boards/{boardID}', 'BoardController@getBoardByID')->name('getBoardByID');
Route::get('boards/user/{userID}', 'BoardController@getBoardsByUserID')->name('getBoardsByUserID');
Route::post('boards','BoardController@addNewBoard')->name('addNewBoard');
Route::put('boards', 'BoardController@updateBoard')->name('updateBoard');
Route::delete('boards/{boardID}', 'BoardController@deleteBoard')->name('deleteBoard');

//todo
Route::get('todos', 'ToDoController@getAllToDos')->name('getAllToDos');
Route::get('todos/user/{userID}', 'ToDoController@getToDosByUserID')->name('getToDosByUserID');
Route::get('todos/board/{boardID}', 'ToDoController@getToDosByBoardID')->name('getToDosByBoardID');
Route::post('todos','ToDoController@addNewToDo')->name('addNewToDo');
Route::put('todos','ToDoController@updateToDo')->name('updateToDo');
Route::delete('todos/{todoID}', 'ToDoController@deleteToDo')->name('deleteToDo');
