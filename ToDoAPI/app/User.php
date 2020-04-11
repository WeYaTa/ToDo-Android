<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    public $timestamps = false;
    protected $fillable =['name','email','password'];

    public function boards()
    {
        return $this->hasMany('App\Board','userID', 'userID');
    }
}
